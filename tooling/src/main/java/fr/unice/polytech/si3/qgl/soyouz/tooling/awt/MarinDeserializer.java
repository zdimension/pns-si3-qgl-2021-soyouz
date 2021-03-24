package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;

import java.io.IOException;

public class MarinDeserializer extends StdDeserializer<Marin> implements ResolvableDeserializer
{

    private final JsonDeserializer<?> defaultDeserializer;
    private final InitGameParameters model;

    public MarinDeserializer(JsonDeserializer<?> defaultDeserializer, InitGameParameters model) {
        super(Marin.class);
        this.defaultDeserializer = defaultDeserializer;
        this.model = model;
    }

    @Override
    public Marin deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException
    {
        String text = jp.getText();
        JsonToken currentToken = jp.getCurrentToken();

        if(!currentToken.equals(JsonToken.VALUE_NUMBER_INT) && !text.startsWith("{")) {
            try{
                return model.getSailorById(Integer.parseInt(text)).orElse(null);
            }
            catch(Exception e) {
                throw new IOException("Unable to process '" + text + "'. Expecting an ID as an integer or a full json representation of the object.");
            }

        }

        return (Marin) defaultDeserializer.deserialize(jp, dc);
    }

    @Override
    public void resolve(DeserializationContext ctxt) throws JsonMappingException
    {
        ((ResolvableDeserializer) defaultDeserializer).resolve(ctxt);
    }
}