package edu.kulikov.email2telegram.spring;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.Reader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 24.09.2016
 */
@Slf4j
public class MessagesResolver {
    private static MessagesResolver messagesResolver;
    private PropertiesConfiguration configuration;

    private MessagesResolver() throws ConfigurationException, URISyntaxException {
        Parameters params = new Parameters();
        URL resource = getClass().getClassLoader().getResource("messages.properties");
      //  String file = currentThread().getContextClassLoader().getResource("messages.properties").getFile();
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
                new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setFileName(resource.toExternalForm())
                                .setEncoding("UTF-8")
                                .setIOFactory(new PropertiesConfiguration.DefaultIOFactory() {
                                    @Override
                                    public PropertiesConfiguration.PropertiesReader createPropertiesReader(Reader in) {
                                        return new EmojiPropertyReader(in);
                                    }

                                    @Override
                                    public PropertiesConfiguration.PropertiesWriter createPropertiesWriter(Writer out, ListDelimiterHandler handler) {
                                        return super.createPropertiesWriter(out, handler);
                                    }
                                }));
        configuration = builder.getConfiguration();
    }

    public static String msg(String key, Object... args) {
        if (messagesResolver == null) {
            try {
                messagesResolver = new MessagesResolver();
            } catch (Exception e) {
                log.error("Can't initiate property resolver", e);
                throw new IllegalStateException(e);
            }
        }
        String value = String.valueOf(messagesResolver.getConfiguration().getProperty(key));
        return MessageFormat.format(value, (Object[]) args);
    }

    private PropertiesConfiguration getConfiguration() {
        return configuration;
    }

    //replaces emoji like :dog: into unicode symbols
    private class EmojiPropertyReader extends PropertiesConfiguration.PropertiesReader {

        EmojiPropertyReader(Reader reader) {
            super(reader);
        }

        @Override
        protected void parseProperty(String line) {
            super.parseProperty(line);
            String propertyValue = getPropertyValue();
            if (propertyValue.contains(":")) {
                String parsed = EmojiParser.parseToUnicode(propertyValue);
                initPropertyValue(parsed);
            }
        }
    }
}



