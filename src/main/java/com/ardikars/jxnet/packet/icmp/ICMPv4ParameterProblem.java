package com.ardikars.jxnet.packet.icmp;

public class ICMPv4ParameterProblem extends ICMPAbstractMessage {

    public static final ICMPv4ParameterProblem POINTER_INDICATES_THE_ERROR =
            new ICMPv4ParameterProblem(ICMPTypeAndCode.newInstance((byte) 12, (byte) 0), "Type: Parameter Problem, Code: Pointer Indicates The Error");

    public static final ICMPv4ParameterProblem MISSING_REQUIRED_OPTION =
            new ICMPv4ParameterProblem(ICMPTypeAndCode.newInstance((byte) 12, (byte) 1), "Type: Parameter Problem, Code: Missing a Required Option");

    public static final ICMPv4ParameterProblem BAD_LENGTH =
            new ICMPv4ParameterProblem(ICMPTypeAndCode.newInstance((byte) 12, (byte) 2), "Type: Parameter Problem, Code: Bad Length");

    protected ICMPv4ParameterProblem(ICMPTypeAndCode typeAndCode, String description) {
        super(typeAndCode, description);
    }

    static {
        ICMPAbstractMessage.register(POINTER_INDICATES_THE_ERROR);
        ICMPAbstractMessage.register(MISSING_REQUIRED_OPTION);
        ICMPAbstractMessage.register(BAD_LENGTH);
    }

}
