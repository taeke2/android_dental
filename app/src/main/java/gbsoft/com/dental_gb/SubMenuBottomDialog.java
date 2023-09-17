package gbsoft.com.dental_gb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import gbsoft.com.dental_gb.databinding.DialogBottomSubMenuBinding;

public class SubMenuBottomDialog extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogBottomSubMenuBinding binding = DialogBottomSubMenuBinding.inflate(inflater, container, false);

        Iconify.with(new FontAwesomeModule());

        Bundle bundle = getArguments();
        String requestCode = bundle.getString("reqNum");
        String clientTel = bundle.getString("clientTel");

        binding.layoutSub1.setVisibility(View.VISIBLE);
        binding.iconTxtImage.setOnClickListener(v1 -> {
            Intent intent_image = new Intent(getActivity(), RequestImageActivity.class);
            intent_image.putExtra("reqNum", requestCode);
            startActivity(intent_image);
        });

        binding.iconTxtCall.setOnClickListener(v12 -> {
            Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + clientTel));
            startActivity(intent);
        });

        binding.iconTxtMemo.setOnClickListener(v13 -> {
            Intent intent = new Intent(getActivity(), MemoActivity.class);
            intent.putExtra("reqNum", requestCode);
            startActivity(intent);
        });

        return binding.getRoot();
    }
}
