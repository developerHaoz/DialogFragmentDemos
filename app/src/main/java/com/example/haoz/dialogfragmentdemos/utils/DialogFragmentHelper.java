package com.example.haoz.dialogfragmentdemos.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.haoz.dialogfragmentdemos.R;

import java.util.Calendar;

/**
 * Created by Haoz on 2017/4/6 0006.
 */

public class DialogFragmentHelper {

    private static final String DIALOG_POSITIVE = "确定";
    private static final String DIALOG_NEGATIVE = "取消";

    private static final String TAG_HEAD = DialogFragmentHelper.class.getSimpleName();

    /**
     * 加载中的弹出窗
     */
    private static final int PROGRESS_THEME = R.style.Base_AlertDialog;
    private static final String PROGRESS_TAG = TAG_HEAD + ":progress";

    public static CommonDialogFragment showProgress(FragmentManager fragmentManager, String message){
        return showProgress(fragmentManager, message, true, null);
    }

    public static CommonDialogFragment showProgress(FragmentManager fragmentManager, String message, boolean cancelable){
        return showProgress(fragmentManager, message, cancelable, null);
    }

    public static CommonDialogFragment showProgress(FragmentManager fragmentManager, final String message, boolean cancelable
            , CommonDialogFragment.OnDialogCancelListener cancelListener){

        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(new CommonDialogFragment.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                ProgressDialog progressDialog = new ProgressDialog(context, PROGRESS_THEME);
                progressDialog.setMessage(message);
                return progressDialog;
            }
        }, cancelable, cancelListener);
        dialogFragment.show(fragmentManager, PROGRESS_TAG);
        return dialogFragment;
    }

    /**
     * 简单提示弹出窗
     */
    private static final int TIPS_THEME = R.style.Base_AlertDialog;
    private static final String TIPS_TAG = TAG_HEAD + ":tips";

    public static void showTips(FragmentManager fragmentManager, String message){
        showTips(fragmentManager, message, true, null);
    }

    public static void showTips(FragmentManager fragmentManager, String message, boolean cancelable){
        showTips(fragmentManager, message, cancelable, null);
    }

    public static void showTips(FragmentManager fragmentManager, final String message, boolean cancelable
            , CommonDialogFragment.OnDialogCancelListener cancelListener){

        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(new CommonDialogFragment.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, TIPS_THEME);
                builder.setMessage(message);
                return builder.create();
            }
        }, cancelable, cancelListener);
        dialogFragment.show(fragmentManager, TIPS_TAG);
    }


    /**
     * 确定取消框
     */
    private static final int CONFIRM_THEME = R.style.Base_AlertDialog;
    private static final String CONfIRM_TAG = TAG_HEAD + ":confirm";

    public static void showConfirmDialog(FragmentManager fragmentManager, final String message, final IDialogResultListener<Integer> listener
            , boolean cancelable, CommonDialogFragment.OnDialogCancelListener cancelListener){
        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(new CommonDialogFragment.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, CONFIRM_THEME);
                builder.setMessage(message);
                builder.setPositiveButton(DIALOG_POSITIVE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listener != null){
                            listener.onDataResult(which);
                        }
                    }
                });
                builder.setNegativeButton(DIALOG_NEGATIVE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listener != null){
                            listener.onDataResult(which);
                        }
                    }
                });
                return builder.create();
            }
        }, cancelable, cancelListener);
        dialogFragment.show(fragmentManager, CONfIRM_TAG);

    }

    /**
     * 带列表的弹出窗
     */
    private static final int LIST_THEME = R.style.Base_AlertDialog;
    private static final String LIST_TAG = TAG_HEAD + ":list";

    public static DialogFragment showListDialog(FragmentManager fragmentManager, final String title, final String[] items
            , final IDialogResultListener<Integer> resultListener, boolean cancelable ){
        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(new CommonDialogFragment.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, LIST_THEME);
                builder.setTitle(title);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(resultListener != null){
                            resultListener.onDataResult(which);
                        }
                    }
                });
                return builder.create();
            }
        }, cancelable, null);
        dialogFragment.show(fragmentManager, LIST_TAG);
        return null;
    }

    /**
     * 选择日期
     */
    private static final int DATE_THEME = R.style.Base_AlertDialog;
    private static final String DATE_TAG = TAG_HEAD + ":date";

    public static DialogFragment showDateDialog(FragmentManager fragmentManager, final String title, final Calendar calendar
            , final IDialogResultListener<Calendar> resultListener, final boolean cancelable){
        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(new CommonDialogFragment.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(context, DATE_THEME, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        resultListener.onDataResult(calendar);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.setTitle(title);
                datePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(DIALOG_POSITIVE);
                        datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setText(DIALOG_NEGATIVE);
                    }
                });
                return datePickerDialog;

            }
        }, cancelable, null);
        dialogFragment.show(fragmentManager, DATE_TAG);
        return null;
    }


    /**
     * 选择时间
     */
    private static final int TIME_THEME = R.style.Base_AlertDialog;
    private static final String TIME_TAG = TAG_HEAD + ":time";
    public static void showTimeDialog(FragmentManager manager, final String title, final Calendar calendar, final IDialogResultListener<Calendar> resultListener, final boolean cancelable){
        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(new CommonDialogFragment.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                final TimePickerDialog dateDialog = new TimePickerDialog(context, TIME_THEME, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(resultListener != null){
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);
                            resultListener.onDataResult(calendar);
                        }
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

                dateDialog.setTitle(title);
                dateDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        dateDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(DIALOG_POSITIVE);
                        dateDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setText(DIALOG_NEGATIVE);
                    }
                });

                return dateDialog;
            }
        }, cancelable, null);
        dialogFragment.show(manager, DATE_TAG);
    }

    /**
     * 带输入框的弹出窗
     */
    private static final int INSERT_THEME = R.style.Base_AlertDialog;
    private static final String INSERT_TAG  = TAG_HEAD + ":insert";

    public static void showInsertDialog(FragmentManager manager, final String title, final IDialogResultListener<String> resultListener, final boolean cancelable){

        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(new CommonDialogFragment.OnCallDialog() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Dialog getDialog(Context context) {
                final EditText editText = new EditText(context);
                editText.setBackground(null);
                editText.setPadding(60, 40, 0, 0);
                AlertDialog.Builder builder = new AlertDialog.Builder(context, INSERT_THEME);
                builder.setTitle(title);
                builder.setView(editText);
                builder.setPositiveButton(DIALOG_POSITIVE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(resultListener != null){
                            resultListener.onDataResult(editText.getText().toString());
                        }
                    }
                });
                builder.setNegativeButton(DIALOG_NEGATIVE, null);
                return builder.create();

            }
        }, cancelable, null);
        dialogFragment.show(manager, INSERT_TAG);

    }


    /**
     * 带输入密码框的弹出窗
     */
    private static final int PASSWORD_INSER_THEME = R.style.Base_AlertDialog;
    private static final String PASSWORD_INSERT_TAG = TAG_HEAD + ":insert";

    public static void showPasswordInsertDialog(FragmentManager manager, final String title, final IDialogResultListener<String> resultListener, final boolean cancelable){
        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(new CommonDialogFragment.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                final EditText editText = new EditText(context);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editText.setEnabled(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(context, PASSWORD_INSER_THEME);
                builder.setTitle(title);
                builder.setView(editText);
                builder.setPositiveButton(DIALOG_POSITIVE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(resultListener != null){
                            resultListener.onDataResult(editText.getText().toString());
                        }
                    }
                });
                builder.setNegativeButton(DIALOG_NEGATIVE, null);
                return builder.create();
            }
        }, cancelable, null);
        dialogFragment.show(manager, INSERT_TAG);
    }

    /**
     * 两个输入框的弹出窗
     */
    private static final int INTERVAL_INSERT_THEME = R.style.Base_AlertDialog;
    private static final String INTERVAL_INSERT_TAG = TAG_HEAD + ":interval_insert";

    public static void showIntervalInsertDialog(FragmentManager manager, final String title, final IDialogResultListener<String []> resultListener, final boolean cancelable){
        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(new CommonDialogFragment.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_interval_insert, null);
                final EditText minEditText = (EditText) view.findViewById(R.id.interval_insert_et_min);
                final EditText maxEditText = (EditText) view.findViewById(R.id.interval_insert_et_max);
                AlertDialog.Builder builder = new AlertDialog.Builder(context, INTERVAL_INSERT_THEME);
                return builder.setTitle(title)
                        .setView(view)
                        .setPositiveButton(DIALOG_POSITIVE, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(resultListener != null){
                                    resultListener.onDataResult(new String[]{minEditText.getText().toString(), maxEditText.getText().toString()});
                                }
                            }
                        }).setNegativeButton(DIALOG_NEGATIVE, null)
                        .create();
            }
        }, cancelable, null);
        dialogFragment.show(manager, INTERVAL_INSERT_TAG);
    }


}

















