package tools.jackson.databind.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.ArrayType;
import tools.jackson.databind.type.ReferenceType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0613F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: JavaTypeTest#testAnchorTypeForRefTypes().
    void anchorTypeForRefTypesVpack() {
        JavaType type = MAPPER.constructType(AtomicStringReference.class);
        assertTrue(type.isReferenceType());
        assertTrue(type.hasContentType());
        JavaType contentType = type.getContentType();
        assertEquals(String.class, contentType.getRawClass());
        assertSame(contentType, type.containedType(0));
        ReferenceType referenceType = (ReferenceType) type;
        assertFalse(referenceType.isAnchorType());
        assertEquals(AtomicReference.class, referenceType.getAnchorType().getRawClass());
    }

    // Provenance: JavaTypeTest#testArrayType().
    void arrayTypeVpack() {
        JavaType type = ArrayType.construct(MAPPER.constructType(String.class), null);
        assertNotNull(type);
        assertTrue(type.isContainerType());
        assertFalse(type.isIterationType());
        assertFalse(type.isReferenceType());
        assertTrue(type.hasContentType());
        assertNotNull(type.toString());
        assertNotNull(type.getContentType());
        assertNull(type.getKeyType());
        assertTrue(type.equals(type));
        assertFalse(type.equals(null));
        assertFalse(type.equals("xyz"));
        assertTrue(type.equals(ArrayType.construct(MAPPER.constructType(String.class), null)));
        assertFalse(type.equals(ArrayType.construct(MAPPER.constructType(Integer.class), null)));
    }
static class Range<E extends Comparable<E>> {
        protected E start, end;
        public Range() { }
        public Range(E start, E end) { this.start = start; this.end = end; }
        public E getEnd() { return end; }
        public void setEnd(E value) { end = value; }
        public E getStart() { return start; }
        public void setStart(E value) { start = value; }
    }
static class DoubleRange extends Range<Double> {
        public DoubleRange() { }
        public DoubleRange(Double start, Double end) { super(start, end); }
    }
static class IntBean implements Serializable {
        public int x;
        public IntBean() { }
        IntBean(int x) { this.x = x; }
    }
static class IntBeanWrapper<T extends IntBean> {
        public T wrapped;
        IntBeanWrapper() { }
        IntBeanWrapper(T value) { wrapped = value; }
    }
static class BoundedWrapper<A extends Serializable> { public List<A> values; }
static class Document { }
static class Row { }
static class RowWithDoc<D extends Document> extends Row { @JsonProperty("d") D d; }
static class ResultSet<R extends Row> { @JsonProperty("rows") List<R> rows; }
static class ResultSetWithDoc<D extends Document> extends ResultSet<RowWithDoc<D>> { }
static class MyDoc extends Document { }
interface AnnotatedValue<E> { String getAnnotation(); E getValue(); }
static class AnnotatedValueSimple<E> implements AnnotatedValue<E> {
        protected E value;
        AnnotatedValueSimple() { }
        AnnotatedValueSimple(E value) { this.value = value; }
        @Override public String getAnnotation() { return null; }
        @Override public E getValue() { return value; }
    }
static class CbFailing<E extends AnnotatedValue<ID>, ID> {
        private E item;
        CbFailing(E item) { this.item = item; }
        public E getItem() { return item; }
        public ID getId() { return item.getValue(); }
    }
static class Result677<T> {
        static class Success677<K> extends Result677<K> {
            public K value;
            public Success677() { }
            Success677(K value) { this.value = value; }
        }
    }
static abstract class BaseType<T> {
        public T value;
        static final class SubType<T extends Number> extends BaseType<T> { }
    }
static class EntityContainer {
        RuleForm entity;
        @SuppressWarnings("unchecked") public <T extends RuleForm> T getEntity() { return (T) entity; }
        public <T extends RuleForm> void setEntity(T value) { entity = value; }
    }
static class RuleForm {
        public int value;
        public RuleForm() { }
        RuleForm(int value) { this.value = value; }
    }
static abstract class Base743<T> { public T inconsequential = null; }
static abstract class BaseData743<T> { public T dataObj; }
static class Child743 extends Base743<Long> {
        static class ChildData extends BaseData743<List<String>> { }
    }
static class StringyList<T extends Serializable> extends ArrayList<T> {
        private static final long serialVersionUID = 1L;
        public StringyList() { }
        @JsonCreator StringyList(List<T> values) { super(values); }
    }
static class AtomicStringReference extends AtomicReference<String> {
        private static final long serialVersionUID = 1L;
    }

    void __invoke_anchorTypeForRefTypesVpack() throws Exception {
        try {
            anchorTypeForRefTypesVpack();
        } finally {
        }
    }


    void __invoke_arrayTypeVpack() throws Exception {
        try {
            arrayTypeVpack();
        } finally {
        }
    }

}
