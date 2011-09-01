package controllers.filters;

public interface IFilter {
    final int FPB_0 = 0;
    final int FPB_10 = 10;
    final int FPB_20 = 20;
    final int FPB_30 = 30;
    final int FPB_40 = 40;
    final int FPB_50 = 50;
    final int FPB_60 = 60;
    final int FPB_70 = 70;
    final int FPB_80 = 80;
    final int FPB_90 = 90;
    final int FPB_100 = 100;
    final int FPB_MAX = FPB_100;
    
    /*
     * Defining the order of before filters
     */
    final int FPB_DEFAULT = FPB_50;
    final int FPB_CONFIG_10 = FPB_10;
    final int FPB_UA_DETECTOR = FPB_20;
    final int FPB_CONFIG_100 = FPB_100;
    
    /*
     * Finally filter priority (should always be in reverse order of FPBs)
     */
    final int FPF_DEFAULT = FPB_MAX - FPB_DEFAULT;
    final int FPF_UA_DETECTOR = FPB_MAX - FPB_UA_DETECTOR;
}
