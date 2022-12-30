package attributes;

public class Enums {

    public enum ElementType {
        ROUTER_G,
        ROUTER_B,
        SERVER,
    }

    public enum CongestionControl {
        SLOW_START,
        CONGESTION_AVOIDANCE,
        WAITING_PKT,
        STOP
    }
}
