package tools.jackson.databind.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Date;
import java.util.function.Predicate;

import tools.jackson.core.util.BufferRecycler;
import tools.jackson.core.util.JsonRecyclerPools;
import tools.jackson.core.util.RecyclerPool;
import tools.jackson.databind.util.BeanUtil;

import static org.junit.jupiter.api.Assertions.assertFalse;

class T32_0622F0 {

    // Provenance: BeanUtilTest#testIsJodaTimeClass().
    void isJodaTimeClassVpack() {
        assertFalse(BeanUtil.isJodaTimeClass(String.class));
        assertFalse(BeanUtil.isJodaTimeClass(Date.class));
        assertFalse(BeanUtil.isJodaTimeClass(java.util.Calendar.class));
    }
static class HybridTestPool implements RecyclerPool<BufferRecycler> {
        private static final long serialVersionUID = 1L;
        private static final Predicate<Thread> IS_VIRTUAL = findIsVirtualPredicate();

        private final RecyclerPool<BufferRecycler> nativePool = JsonRecyclerPools.threadLocalPool();
        private final RecyclerPool<BufferRecycler> virtualPool = JsonRecyclerPools.newConcurrentDequePool();

        @Override
        public BufferRecycler acquirePooled() {
            return (IS_VIRTUAL != null && IS_VIRTUAL.test(Thread.currentThread())
                    ? virtualPool : nativePool).acquirePooled();
        }

        @Override
        public void releasePooled(BufferRecycler pooled) {
            (IS_VIRTUAL != null && IS_VIRTUAL.test(Thread.currentThread())
                    ? virtualPool : nativePool).releasePooled(pooled);
        }

        private static Predicate<Thread> findIsVirtualPredicate() {
            try {
                MethodHandle isVirtual = MethodHandles.publicLookup().findVirtual(Thread.class,
                        "isVirtual", MethodType.methodType(boolean.class));
                return thread -> {
                    try {
                        return (boolean) isVirtual.invoke(thread);
                    } catch (Throwable e) {
                        throw new IllegalStateException(e);
                    }
                };
            } catch (Exception e) {
                return null;
            }
        }
    }

    void __invoke_isJodaTimeClassVpack() throws Exception {
        try {
            isJodaTimeClassVpack();
        } finally {
        }
    }

}
