package value_object.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import value_object.Dinero;

import java.math.BigDecimal;
@Converter
public class DineroAttributeConverter implements AttributeConverter<Dinero, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(Dinero attribute) {
        return attribute == null ? null : attribute.toBigDecimal();
    }

    @Override
    public Dinero convertToEntityAttribute(BigDecimal dbData) {
        return dbData == null ? null : new Dinero(dbData);
    }
}
