package gbsoft.com.dental_gb;

public interface OrderCommitDialogClickListener {
    void processCommitClick(String productInfoCode, String remark, int processState, int processIdx, int artiCode);
    void processFaultyClick(String productInfoCode, String faultyType, String faultyHistory, int processIdx);
}
