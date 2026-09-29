package tools.jackson.databind.jsontype.ext;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;
import tools.jackson.databind.annotation.JsonTypeIdResolver;
import tools.jackson.databind.jsontype.impl.TypeIdResolverBase;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0443F2 {
private static final byte[] EXTERNAL_ARRAY = VPackWireFixtureTest.hex(
            "06 1f 03 43 63 61 74 45 69 64 31 32 33 "
          + "14 0f 44 6e 61 6d 65 46 46 6c 75 66 66 79 01 03 07 0d");
private static final byte[] JSON_VALUE_NON_NULL = VPackWireFixtureTest.hex(
            "0b 4e 02 45 76 61 6c 75 65 46 66 6f 6f 62 61 72" +
                "44 74 79 70 65 76 74 6f 6f 6c 73 2e 6a 61 63 6b" +
                "73 6f 6e 2e 64 61 74 61 62 69 6e 64 2e 6a 73 6f" +
                "6e 74 79 70 65 2e 65 78 74 2e 54 33 32 5f 30 34" +
                "34 33 46 32 24 46 6f 6f 54 79 70 65 10 03");
private static final byte[] CUSTOM_RESOLVER = VPackWireFixtureTest.hex(
            "14 3b 4f 66 6f 72 6d 5f 6f 66 5f 70 61 79 6d 65 6e 74 "
          + "56 49 4e 44 49 56 49 44 55 41 4c 5f 43 52 45 44 49 54 5f 43 41 52 44 "
          + "4f 70 61 79 6d 65 6e 74 5f 64 65 74 61 69 6c 73 0a 02");
private static final byte[] CUSTOM_RESOLVER_BUILDER = VPackWireFixtureTest.hex(
            "14 3d 4f 66 6f 72 6d 5f 6f 66 5f 70 61 79 6d 65 6e 74 "
          + "58 49 4e 53 54 52 55 4d 45 4e 54 45 44 5f 43 52 45 44 49 54 5f 43 41 52 44 "
          + "4f 70 61 79 6d 65 6e 74 5f 64 65 74 61 69 6c 73 0a 02");
private static final byte[] JSON_VALUE_NULL_BEAN = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 18 03");
private final ObjectMapper mapper = new VPackMapper();

    // Provenance: ExternalTypeCustomResolver1288Test#testExternalWithCustomResolver().
    void testExternalWithCustomResolverVpack() throws Exception {
        ClassesWithoutBuilder.PaymentMean result = mapper.readValue(CUSTOM_RESOLVER,
                ClassesWithoutBuilder.PaymentMean.class);
        assertNotNull(result);
        assertInstanceOf(ClassesWithoutBuilder.CreditCardDetails.class, result.paymentDetails);
    }

    // Provenance: ExternalTypeCustomResolver1288Test#testExternalWithCustomResolverAndBuilder().
    void testExternalWithCustomResolverAndBuilderVpack() throws Exception {
        ClassesWithBuilder.PaymentMean result = mapper.readValue(CUSTOM_RESOLVER_BUILDER,
                ClassesWithBuilder.PaymentMean.class);
        assertNotNull(result);
        assertInstanceOf(ClassesWithBuilder.EncryptedCreditCardDetails.class, result.paymentDetails);
    }
static class Container {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type", visible = true)
        @JsonSubTypes(@JsonSubTypes.Type(FooType.class))
        protected GenericType value;
        public GenericType getValue() { return value; }
        public void setValue(GenericType value) { this.value = value; }
    }
interface GenericType {
        String getValue();
        void setValue(String value);
    }
static abstract class AbstractGenericType implements GenericType {
        protected String value;
        AbstractGenericType() { }
        AbstractGenericType(String value) { this.value = value; }
        @Override @JsonValue public String getValue() { return value; }
        @Override public void setValue(String value) { this.value = value; }
    }
static class FooType extends AbstractGenericType {
        FooType() { }
        @JsonCreator FooType(String value) { super(value); }
    }
@com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({"type", "uniqueId", "animal"})
    static class WrapperWithExternalProperty {
        public String type;
        public String uniqueId;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type")
        @JsonSubTypes(@JsonSubTypes.Type(value = Cat.class, name = "cat"))
        public Animal animal;
    }
static class Animal { public String name; }
static class Cat extends Animal { }
static class ClassesWithoutBuilder {
        static class PaymentMean {
            @JsonProperty("form_of_payment")
            public FormOfPayment formOfPayment;
            @JsonProperty("payment_details")
            @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                    property = "form_of_payment", visible = true)
            @JsonTypeIdResolver(PaymentDetailsTypeIdResolver.class)
            public PaymentDetails paymentDetails;
        }

        interface PaymentDetails { }
        static class CreditCardDetails implements PaymentDetails {
            public String name;
        }
        enum FormOfPayment { INDIVIDUAL_CREDIT_CARD(CreditCardDetails.class) ;
            final Class<? extends PaymentDetails> type;
            FormOfPayment(Class<? extends PaymentDetails> type) { this.type = type; }
            static FormOfPayment fromDetailsClass(Class<?> type) {
                for (FormOfPayment value : values()) if (value.type == type) return value;
                throw new IllegalArgumentException("not found");
            }
        }
        static class PaymentDetailsTypeIdResolver extends TypeIdResolverBase {
            @Override public String idFromValue(DatabindContext ctxt, Object value) {
                return FormOfPayment.fromDetailsClass(value.getClass()).name();
            }
            @Override public String idFromValueAndType(DatabindContext ctxt, Object value,
                    Class<?> suggestedType) { return idFromValue(ctxt, value); }
            @Override public JavaType typeFromId(DatabindContext ctxt, String id) {
                return ctxt.constructType(FormOfPayment.valueOf(id).type);
            }
            @Override public String getDescForKnownTypeIds() { return "PaymentDetails"; }
            @Override public JsonTypeInfo.Id getMechanism() { return JsonTypeInfo.Id.CUSTOM; }
        }
    }
static class ClassesWithBuilder {
        @JsonDeserialize(builder = PaymentMean.Builder.class)
        static class PaymentMean {
            final FormOfPayment formOfPayment;
            final PaymentDetails paymentDetails;
            PaymentMean(FormOfPayment formOfPayment, PaymentDetails paymentDetails) {
                this.formOfPayment = formOfPayment;
                this.paymentDetails = paymentDetails;
            }
            @JsonPOJOBuilder(withPrefix = "")
            static class Builder {
                private FormOfPayment formOfPayment;
                private PaymentDetails paymentDetails;
                @JsonProperty("form_of_payment")
                public Builder formOfPayment(FormOfPayment value) { formOfPayment = value; return this; }
                @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                        property = "form_of_payment", visible = true)
                @JsonTypeIdResolver(PaymentDetailsTypeIdResolver.class)
                @JsonProperty("payment_details")
                public Builder paymentDetails(PaymentDetails value) { paymentDetails = value; return this; }
                public PaymentMean build() { return new PaymentMean(formOfPayment, paymentDetails); }
            }
        }
        interface PaymentDetails { }
        static class EncryptedCreditCardDetails implements PaymentDetails { public String name; }
        enum FormOfPayment { INSTRUMENTED_CREDIT_CARD(EncryptedCreditCardDetails.class) ;
            final Class<? extends PaymentDetails> type;
            FormOfPayment(Class<? extends PaymentDetails> type) { this.type = type; }
            static FormOfPayment fromDetailsClass(Class<?> type) {
                for (FormOfPayment value : values()) if (value.type == type) return value;
                throw new IllegalArgumentException("not found");
            }
        }
        static class PaymentDetailsTypeIdResolver extends TypeIdResolverBase {
            @Override public String idFromValue(DatabindContext ctxt, Object value) {
                return FormOfPayment.fromDetailsClass(value.getClass()).name();
            }
            @Override public String idFromValueAndType(DatabindContext ctxt, Object value,
                    Class<?> suggestedType) { return idFromValue(ctxt, value); }
            @Override public JavaType typeFromId(DatabindContext ctxt, String id) {
                return ctxt.constructType(FormOfPayment.valueOf(id).type);
            }
            @Override public String getDescForKnownTypeIds() { return "PaymentDetails"; }
            @Override public JsonTypeInfo.Id getMechanism() { return JsonTypeInfo.Id.CUSTOM; }
        }
    }

    void __invoke_testExternalWithCustomResolverVpack() throws Exception {
        try {
            testExternalWithCustomResolverVpack();
        } finally {
        }
    }


    void __invoke_testExternalWithCustomResolverAndBuilderVpack() throws Exception {
        try {
            testExternalWithCustomResolverAndBuilderVpack();
        } finally {
        }
    }

}
