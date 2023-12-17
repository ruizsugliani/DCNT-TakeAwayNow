package value_object.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import value_object.PuntosDeConfianza;


@Converter
public class PuntosDeConfianzaAttributeConverter implements AttributeConverter<PuntosDeConfianza, Integer> {

    @Override
    public Integer convertToDatabaseColumn(PuntosDeConfianza attribute) {
        return attribute == null ? null : attribute.toInt();
    }

    @Override
    public PuntosDeConfianza convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : new PuntosDeConfianza(dbData);
    }
}
