import com.ardikars.ann.ActivationFunctions;
import com.ardikars.ann.NeuralNetwork;
import com.ardikars.jxnet.Inet4Address;
import com.ardikars.jxnet.MacAddress;
import com.ardikars.tulip.StaticField;
import com.ardikars.tulip.TULIP;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by root on 06/04/17.
 */
public class First {

    public static void main(String[] args) {
        Properties hiddenWeight = null;
        Properties outputWeight = null;

        FileReader hiddenReader;
        FileReader outputReader;

        try {
            hiddenWeight = new Properties();
            hiddenReader = new FileReader("hidden-weight.properties");
            hiddenWeight.load(hiddenReader);
            hiddenReader.close();

            outputWeight = new Properties();
            outputReader = new FileReader("output-weight.properties");
            outputWeight.load(outputReader);
            outputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> hiddenMap = new HashMap<String, String>((Map) hiddenWeight);
        Map<String, String> outputnMap = new HashMap<String, String>((Map) outputWeight);

        StaticField.hiddenMap = hiddenMap;
        StaticField.outputMap = outputnMap;

        double[][] inputs = new double[][] {
                TULIP.array(0, 0, 0, 0, 0.5)
        };

        double result = NeuralNetwork.simuff(inputs, StaticField.hiddenMap, StaticField.outputMap,
                ActivationFunctions.Type.SIGMOID, 5, 1).getOutput()[0];
    }
}
