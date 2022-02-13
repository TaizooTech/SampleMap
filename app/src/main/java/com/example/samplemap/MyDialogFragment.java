package com.example.samplemap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {

    private OnDialogFragmentListener listener;

    public interface OnDialogFragmentListener {
        void onDialogResult(InfoContents contents);
    }

    public void setDialogFragmentListener(OnDialogFragmentListener listener) {
        this.listener = listener;
    }

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // カスタムダイアログのビューを生成
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.custom_dialog, null);

        // 吹き出しに表示する画像を選択するスピナーを生成
        SpinnerAdapter adapter = new SpinnerAdapter(getActivity());
        Spinner spinner = dialogView.findViewById(R.id.sp_icon);
        spinner.setAdapter(adapter);

        // ダイアログの作成
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ダイアログでOKをクリックした場合の操作
                        if (null != listener) {
                            // リスナー登録されている場合、リソースIDとタイトルと説明の情報を返却する

                            InfoContents contents = new InfoContents();
                            contents.resourceId = (int)adapter.getItem(spinner.getSelectedItemPosition());
                            contents.title = ((EditText) dialogView.findViewById(R.id.et_title)).getText().toString();
                            contents.snippet = ((EditText) dialogView.findViewById(R.id.et_snipet)).getText().toString();
                            listener.onDialogResult(contents);
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    /**
     * 吹き出しに表示するコンテンツを保持するクラス
     */
    class InfoContents {
        int resourceId;
        String title;
        String snippet;
    }
}
