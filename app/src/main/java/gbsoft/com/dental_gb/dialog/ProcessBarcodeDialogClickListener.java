package gbsoft.com.dental_gb.dialog;

public interface ProcessBarcodeDialogClickListener {
    void onStartClick(int artiBox, int artiIdx, ProcessBarcodeDialog dialog);
    void onFinishClick(int artiBox, int artiIdx, ProcessBarcodeDialog dialog);
}
