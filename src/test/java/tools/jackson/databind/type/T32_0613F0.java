package tools.jackson.databind.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0613F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: GenericTypeTest#testLowerBound().
    void lowerBoundVpack() throws Exception {
        IntBeanWrapper<?> result = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new IntBeanWrapper<>(new IntBean(3))), IntBeanWrapper.class);
        assertNotNull(result);
        assertEquals(IntBean.class, result.wrapped.getClass());
        assertEquals(3, result.wrapped.x);
    }

    // Provenance: GenericTypeTest#testBounded().
    void boundedVpack() throws Exception {
        BoundedWrapper<IntBean> input = new BoundedWrapper<>();
        input.values = List.of(new IntBean(3));
        BoundedWrapper<IntBean> result = MAPPER.readValue(MAPPER.writeValueAsBytes(input),
                new TypeReference<BoundedWrapper<IntBean>>() { });
        assertEquals(1, result.values.size());
        assertEquals(IntBean.class, result.values.get(0).getClass());
        assertEquals(3, result.values.get(0).x);
    }

    // Provenance: GenericTypeTest#testGenericsComplex().
    void genericsComplexVpack() throws Exception {
        DoubleRange input = new DoubleRange(-0.5, 0.5);
        DoubleRange result = MAPPER.readValue(MAPPER.writeValueAsBytes(input), DoubleRange.class);
        assertNotNull(result);
        assertEquals(-0.5, result.start);
        assertEquals(0.5, result.end);
    }

    // Provenance: GenericTypeTest#testIssue778().
    void issue778Vpack() throws Exception {
        TypeReference<?> typeRef = new TypeReference<ResultSetWithDoc<MyDoc>>() { };
        JavaType type = MAPPER.getTypeFactory().constructType(typeRef);
        JavaType resultSetType = type.findSuperType(ResultSet.class);
        assertNotNull(resultSetType);
        assertEquals(1, resultSetType.containedTypeCount());
        JavaType rowType = resultSetType.containedType(0);
        assertNotNull(rowType);
        assertEquals(RowWithDoc.class, rowType.getRawClass());
        assertEquals(1, rowType.containedTypeCount());
        assertEquals(MyDoc.class, rowType.containedType(0).getRawClass());

        ResultSetWithDoc<MyDoc> input = new ResultSetWithDoc<>();
        RowWithDoc<MyDoc> row = new RowWithDoc<>();
        row.d = new MyDoc();
        input.rows = List.of(row);
        ResultSetWithDoc<MyDoc> result = MAPPER.readValue(MAPPER.writeValueAsBytes(input), type);
        assertEquals(MyDoc.class, result.rows.get(0).d.getClass());
    }

    // Provenance: GenericTypeTest#testCrossReferencingGenericBounds().
    void crossReferencingGenericBoundsVpack() throws Exception {
        AnnotatedValueSimple<Integer> item = new AnnotatedValueSimple<>(5);
        CbFailing<AnnotatedValueSimple<Integer>, Integer> input = new CbFailing<>(item);
        byte[] encoded = MAPPER.writeValueAsBytes(input);
        assertNotNull(encoded);
    }

    // Provenance: GenericTypeTest#testGenericFieldInSubtype677().
    void genericFieldInSubtype677Vpack() throws Exception {
        Result677.Success677<Integer> input = new Result677.Success677<>(4);
        byte[] encoded = MAPPER.writeValueAsBytes(input);
        assertEquals(Result677.Success677.class, MAPPER.readValue(encoded,
                Result677.Success677.class).getClass());
        assertEquals(4, MAPPER.readValue(encoded,
                new TypeReference<Result677.Success677<Integer>>() { }).value);
    }

    // Provenance: GenericTypeTest#testInnerTypeWithBounds().
    void innerTypeWithBoundsVpack() throws Exception {
        BaseType.SubType<Integer> input = new BaseType.SubType<>();
        input.value = 9;
        BaseType.SubType<?> result = MAPPER.readValue(MAPPER.writeValueAsBytes(input),
                BaseType.SubType.class);
        assertNotNull(result);
        assertEquals(9, result.value);
    }

    // Provenance: GenericTypeTest#testLocalPartialType609().
    void localPartialType609Vpack() throws Exception {
        EntityContainer input = new EntityContainer();
        input.setEntity(new RuleForm(12));
        EntityContainer result = MAPPER.readValue(MAPPER.writeValueAsBytes(input), EntityContainer.class);
        assertEquals(12, result.getEntity().value);
    }

    // Provenance: GenericTypeTest#testAliasResolutionIssue743().
    void aliasResolutionIssue743Vpack() throws Exception {
        Child743.ChildData input = new Child743.ChildData();
        input.dataObj = List.of("one", "two", "three");
        Child743.ChildData result = MAPPER.readValue(MAPPER.writeValueAsBytes(input),
                Child743.ChildData.class);
        assertNotNull(result.dataObj);
        assertEquals(3, result.dataObj.size());
    }

    // Provenance: GenericTypeTest#testPolymorphicWithOverride().
    void polymorphicWithOverrideVpack() throws Exception {
        JavaType type = MAPPER.getTypeFactory().constructCollectionType(StringyList.class, String.class);
        StringyList<String> input = new StringyList<>();
        input.add("value 1");
        input.add("value 2");
        StringyList<String> result = MAPPER.readValue(MAPPER.writeValueAsBytes(input), type);
        assertNotNull(result);
        assertEquals(List.of("value 1", "value 2"), result);
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

    void __invoke_lowerBoundVpack() throws Exception {
        try {
            lowerBoundVpack();
        } finally {
        }
    }


    void __invoke_boundedVpack() throws Exception {
        try {
            boundedVpack();
        } finally {
        }
    }


    void __invoke_genericsComplexVpack() throws Exception {
        try {
            genericsComplexVpack();
        } finally {
        }
    }


    void __invoke_issue778Vpack() throws Exception {
        try {
            issue778Vpack();
        } finally {
        }
    }


    void __invoke_crossReferencingGenericBoundsVpack() throws Exception {
        try {
            crossReferencingGenericBoundsVpack();
        } finally {
        }
    }


    void __invoke_genericFieldInSubtype677Vpack() throws Exception {
        try {
            genericFieldInSubtype677Vpack();
        } finally {
        }
    }


    void __invoke_innerTypeWithBoundsVpack() throws Exception {
        try {
            innerTypeWithBoundsVpack();
        } finally {
        }
    }


    void __invoke_localPartialType609Vpack() throws Exception {
        try {
            localPartialType609Vpack();
        } finally {
        }
    }


    void __invoke_aliasResolutionIssue743Vpack() throws Exception {
        try {
            aliasResolutionIssue743Vpack();
        } finally {
        }
    }


    void __invoke_polymorphicWithOverrideVpack() throws Exception {
        try {
            polymorphicWithOverrideVpack();
        } finally {
        }
    }

}
