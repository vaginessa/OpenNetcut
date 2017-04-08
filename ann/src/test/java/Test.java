import com.ardikars.ann.*;

import java.util.List;

/**
 * Created by root on 07/04/17.
 */
public class Test {

    public static void main(String[] args) {
        Logger<String> longger = (arg, neuron, connection, printble) -> {

        };

        ParamBuilder<String> params = ParamBuilder
                .buildParameters(longger, null, 100, 1000, 0.1, 0.1);

        NeuralNetwork nn = NeuralNetwork.initff(ANN.generateInputs(),
                6, ANN.generateOutputs());
        nn.trainbp(ActivationFunctions.Type.SIGMOID, params);

        NeuralNetwork.simuff(ANN.generateInputs(), nn.getWeight1(), nn.getWeight2(),
                ActivationFunctions.Type.SIGMOID, 6, 1);


    }



}
