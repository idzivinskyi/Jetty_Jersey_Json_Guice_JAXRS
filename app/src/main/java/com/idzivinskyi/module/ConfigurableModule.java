package com.idzivinskyi.module;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class ConfigurableModule extends AbstractModule {


    private static final Logger log = LoggerFactory.getLogger(ConfigurableModule.class);
    private static final String CONFIG_HOME = "CONF_DIR";
    protected final Properties appProperties = new Properties();
    protected final String configName;

    protected ConfigurableModule(String configName) {
        this.configName = configName;
    }

    @Override
    protected void configure() {
        updateAndAddProperties(binder(), configName);
        customProperties(binder());
        loadLocalAppProperties(binder());
        customConfigure(binder());

        applyProperties(binder());
    }

    protected void customConfigure(Binder binder) {

    }

    protected void customProperties(Binder binder) {

    }

    private void applyProperties(Binder binder) {
        final StringBuilder prop = new StringBuilder("PROPERTIES [\n");
        appProperties.forEach((k, v) -> prop.append(String.format("    %s = %s\n", k, v)));
        log.info(prop.append("]").toString());
        Names.bindProperties(binder, appProperties);
    }

    private void loadLocalAppProperties(Binder binder) {
        File appFile = new File(System.getenv(CONFIG_HOME), configName);
        if (appFile.exists()) {
            try (InputStream stream = new FileInputStream(appFile)) {
                appProperties.load(stream);
            } catch (IOException e) {
                log.error("Cannot create steam for '" + configName + "' file.", e);
                binder.addError(e);
            }
        }
    }

    protected void updateAndAddProperties(Binder binder, String configName) {
        try (InputStream stream = getClass().getResourceAsStream("/" + configName)) {
            if (stream == null) return;
            appProperties.load(stream);
        } catch (IOException e) {
            log.error("Cannot create steam for '" + configName + "' file.", e);
            binder.addError(e);
        }
    }

    public static Properties loadProperties(String name) {
        String propFileName = name + ".properties";

        try {
            Properties defProps = new Properties();
            try (InputStream propStream = ConfigurableModule.class.getClassLoader().getResourceAsStream(propFileName)) {
                if (propStream != null) {
                    defProps.load(propStream);
                }
            }

            Properties props = new Properties();
            props.putAll(defProps);
            File confFile = new File(System.getenv(CONFIG_HOME), propFileName);
            if (confFile.exists()) {
                try (FileInputStream is = new FileInputStream(confFile)) {
                    props.load(is);
                }
            }
            return props;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
