#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
[[ $(uname -s) == Linux ]] || { echo 'Native interop currently requires Linux' >&2; exit 1; }
for tool in git curl tar sha256sum "${CXX:-g++}"; do command -v "$tool" >/dev/null || { echo "Missing prerequisite: $tool" >&2; exit 1; }; done
mkdir -p target/tools target/reports
if command -v cmake >/dev/null; then cmake_bin=$(command -v cmake); else
  case $(uname -m) in x86_64) arch=x86_64;; aarch64) arch=aarch64;; *) echo 'Unsupported Linux architecture' >&2; exit 1;; esac
  archive="cmake-3.31.6-linux-$arch.tar.gz"
  cmake_bin="$PWD/target/tools/cmake-3.31.6-linux-$arch/bin/cmake"
  if [[ ! -x $cmake_bin ]]; then
    base=https://github.com/Kitware/CMake/releases/download/v3.31.6
    curl -fsSL --retry 3 "$base/$archive" -o "target/tools/$archive"
    curl -fsSL --retry 3 "$base/cmake-3.31.6-SHA-256.txt" -o target/tools/cmake-checksums.txt
    (cd target/tools; awk -v a="$archive" '$2 == a {print}' cmake-checksums.txt > selected.sha256; [[ -s selected.sha256 ]]; sha256sum -c selected.sha256)
    tar -xzf "target/tools/$archive" -C target/tools
  fi
fi
source_dir="$PWD/target/velocypack"
if [[ ! -d $source_dir/.git ]]; then git clone --branch main https://github.com/arangodb/velocypack.git "$source_dir"; fi
ref=${VPACK_REF:-1450560e980140578eb050199f58c4351a5d13d5}
if [[ -n ${VPACK_REF:-} ]]; then git -C "$source_dir" fetch origin --tags; fi
[[ -z $(git -C "$source_dir" status --porcelain) ]] || { echo 'Native source checkout is modified; refusing to use it' >&2; exit 1; }
git -C "$source_dir" -c advice.detachedHead=false checkout --detach "$ref"
revision=$(git -C "$source_dir" rev-parse HEAD)
"$cmake_bin" -S native -B target/native-build -DVPACK_SOURCE="$source_dir" -DVPACK_REVISION="$revision" -DCMAKE_BUILD_TYPE=Release -DCMAKE_CXX_COMPILER="${CXX:-g++}"
"$cmake_bin" --build target/native-build --target vpack_reference --parallel "${BUILD_JOBS:-2}"
{
  echo "native.revision=$revision"
  echo "native.reference=$ref"
  echo 'native.buildOptions=C++20;Release;PIC=ON;UseIPO=OFF;BuildSseOpt=OFF;HashType=xxhash'
  echo "native.cmake=$("$cmake_bin" --version | head -1)"
  echo "native.compiler=$("${CXX:-g++}" --version | head -1)"
  echo "native.git=$(git --version)"
} > target/native.properties
