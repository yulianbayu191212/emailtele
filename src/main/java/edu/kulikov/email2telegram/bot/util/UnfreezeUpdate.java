package edu.kulikov.email2telegram.bot.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.telegram.telegrambots.api.objects.Update;

import java.io.StringWriter;

/**
 * Emulates updates from Telegram to continue using state model which is in {@link FreezeState}
 *
 * @author Andrey Kulikov (ankulikov)
 * @date 11.09.2016
 */
public class UnfreezeUpdate extends Update {
    public UnfreezeUpdate(Update copyFrom, String text) {
        super(makeUpdate(copyFrom, text));
    }

    private static JSONObject makeUpdate(Update source, String text)  {
         try {
             StringWriter stringWriter = new StringWriter();
             JsonGenerator jsonGenerator = new JsonFactory().createGenerator(stringWriter);
             jsonGenerator.setCodec(new ObjectMapper());
             source.serialize(jsonGenerator, null);
             String jsonUpdate = stringWriter.toString();
             stringWriter.getBuffer().setLength(0); //clear buffer

             JSONObject jsonObject = new JSONObject(jsonUpdate);
             jsonObject.getJSONObject("message").put("text", text);
             return jsonObject;
         } catch (Exception ignore) {
             return new JSONObject();
         }

    }
}


