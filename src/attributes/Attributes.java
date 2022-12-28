package attributes;

public class Attributes {

    public enum ElementType {
        ROUTER_G,
        ROUTER_B,
        SERVER,
    }

    public enum CongestionControl {
        SLOW_START,
        CONGESTION_AVOIDANCE
    }
}
