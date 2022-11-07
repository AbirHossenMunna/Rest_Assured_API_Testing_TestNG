package Utils;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Utils {
    public static void setCollectionVariable(String key,String value) throws ConfigurationException {
        PropertiesConfiguration propertiesConfiguration=new PropertiesConfiguration("./src/test/resources/config.properties");
        propertiesConfiguration.setProperty(key,value);
        propertiesConfiguration.save();
    }
}
