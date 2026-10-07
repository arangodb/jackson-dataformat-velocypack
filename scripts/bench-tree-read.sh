#!/usr/bin/env bash
# Usage: scripts/bench-tree-read.sh [prepare|acceptance|diagnostics|profile|all]
# Reuse LOCK_DIR for every before/after comparison. No Jackson override.
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."
exec python3 scripts/reader-performance/run.py "${1:-acceptance}"
