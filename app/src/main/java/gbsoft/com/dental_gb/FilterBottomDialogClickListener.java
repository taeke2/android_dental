package gbsoft.com.dental_gb;

public interface FilterBottomDialogClickListener {
    void onSearchClick(
            String ordDate,
            String deadDate,
            String outDate,
            String manager,
            String searchClient,
            String searchPatient,
            String dent,
            String release
    );
}
