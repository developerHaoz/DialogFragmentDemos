### 前言
> 具体的代码以及示例我都放上 Github 了，有需要的朋友可以去看一下 **[DialogFragmentDemos](https://github.com/developerHaoz/DialogFragmentDemos)**，欢迎 star 和 fork.
#### 本文的主要内容
- DialogFragment 是什么
- 创建通用的 CommonDialogFragment
- 实现各种类型的 DialogFragment

**在写正文之前，先来一波效果展示吧**
![DialogFragmentDemos.gif](http://upload-images.jianshu.io/upload_images/4334738-987d3051423eaf57.gif?imageMogr2/auto-orient/strip)

### 一、DialogFragment 是什么
DialogFragment 在 Android 3.0 时被引入，是一种特殊的 Fragment，用于在 Activity  的内容之上显示一个静态的对话框。例如：警告框、输入框、确认框等。

#### 1、DialogFragment 的优点
其实在 Android 中显示对话框有两种类型可供使用，一种是 DialogFragment，而另一种则是 Dialog。在 DialogFragment 产生之前，我们创建对话框一般采用 Dialog，而且从代码的编写角度来看，Dialog 使用起来其实更加简单，但是 Google 却是推荐尽量使用 DialogFragment，是不是感觉很奇怪，其实原因也很简单， **DialogFragment 有着 Dialog 所没有的非常好的特性**
- DialogFragment 本身是 Fragment 的子类，有着和 Fragment 基本一样的生命周期，使用 DialogFragment 来管理对话框，当旋转屏幕和按下后退键的时候可以更好的管理其生命周期
- 在手机配置变化导致 Activity 需要重新创建时，例如旋转屏幕，基于 DialogFragment 的对话框将会由 FragmentManager 自动重建，然而基于 Dialog 实现的对话框却没有这样的能力

### 2、DialogFragment 的使用
使用 DialogFragment 至少需要实现 onCreateView() 或者 onCreateDialog() 方法，onCreateView() 即使用自定义的 xml 布局文件来展示 Dialog，而 onCreateDialog() 即使用 AlertDialog 或者 Dialog 创建出 我们想要的 Dialog，因为这篇文章主要是讲 DialogFragment 的封装，至于 DialogFragment 具体的使用，可以参考下洋神的这篇文章 [Android 官方推荐 : DialogFragment 创建对话框](http://blog.csdn.net/lmj623565791/article/details/37815413)

### 二、创建通用的 CommonDialogFragment
这个类是 DialogFragment 的子类，对 DialogFragment 进行封装，依赖外部传入的 AlertDialog 来构建，同时也处理了 DialogFragment 中 AlertDialog 不能设置外部取消的问题

```
public class CommonDialogFragment extends DialogFragment {

    /**
     * 监听弹出窗是否被取消
     */
    private OnDialogCancelListener mCancelListener;

    /**
     * 回调获得需要显示的dialog
     */
    private OnCallDialog mOnCallDialog;

    public interface OnDialogCancelListener {
        void onCancel();
    }

    public interface OnCallDialog {
        Dialog getDialog(Context context);
    }

    public static CommonDialogFragment newInstance(OnCallDialog call, boolean cancelable) {
        return newInstance(call, cancelable, null);
    }

    public static CommonDialogFragment newInstance(OnCallDialog call, boolean cancelable, OnDialogCancelListener cancelListener) {
        CommonDialogFragment instance = new CommonDialogFragment();
        instance.setCancelable(cancelable);
        instance.mCancelListener = cancelListener;
        instance.mOnCallDialog = call;
        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (null == mOnCallDialog) {
            super.onCreateDialog(savedInstanceState);
        }
        return mOnCallDialog.getDialog(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            //在5.0以下的版本会出现白色背景边框，若在5.0以上设置则会造成文字部分的背景也变成透明
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                //目前只有这两个dialog会出现边框
                if(dialog instanceof ProgressDialog || dialog instanceof DatePickerDialog) {
                    getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
            }
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.0f;
            window.setAttributes(windowParams);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mCancelListener != null) {
            mCancelListener.onCancel();
        }
    }

}
```
可以看到这个类的代码量也是很少的，先定义了两个接口 OnDialogCancelListener，OnCallDialog，前者用于监听弹出窗是否被取消，后者则可以让我们回调获得想要显示的 Dialog，可以看到在 onCreateDialog() 中我们返回的 是 `mOnCallDialog.getDialog(getActivity);`，当我们在传入 Dialog 的时候，便会回调到此处，让 onCreateDialog() 返回我们传入的 Dialog，对`接口回调`不是很清楚的朋友，可以看下这篇文章 [一个经典例子让你彻彻底底理解java回调机制](http://blog.csdn.net/xiaanming/article/details/8703708)

接着在 onStart() 中进行了一些特殊性的处理，因为在 5.0 以下的版本，ProgressDialog 和 DatePickerDialog 会出现白色的边框，这使得用户体验非常不好，所以我们要在此处进行相应的处理

最后便是封装我们的构造函数 
`newInstance(OnCallDialog call, boolean cancelable, OnDialogCancelListener cancelListener) `，当我们要使用这个 CommonDialogFragment 的时候，先 new 一个 OnCallDialog，将我们想要显示的 Dialog 传进去，`cancelable`，用于设置对话框是否能被取消，可以看到在 onCancel() 有这样一段代码
```
if(mCancelListener != null){
  mCancelListener.onCancel();
}
```
这便是我们在构造函数中传入 OnCancelListener 的原因，当我们想要做一些取消对话框后的处理时，只要在构造函数中传入 OnCancelListener，实现 onCancel() 方法就行了

### 三、实现各种类型的 DialogFragment
既然前面我们创建了 CommonFragment 作为所有 DialogFragment 的基类，那么接下来我们当然要好好地来实现各种类型的 DialogFragment 了，我的思路是创建一个 DialogFragmentHelper 作为实现提示框的帮助类，帮我们把代码都封装起来，使用的时候只需要关注与 AlertDialog 的交互，Helper 会帮助我们用 DialogFragment 来进行显示，这样既能统一整个应用的 Dialog 风格，又能让我们实现各种各样的对话框变得相当的简单

在实现 DialogFragmentHelper 之前我们有两件事先要做一下
##### 1、在 styles 文件中定义我们定义我们对话框的风格样式
```
<style name="Base_AlertDialog" parent="Base.Theme.AppCompat.Light.Dialog">

        <!--不设置在6.0以上会出现，宽度不充满屏幕的情况-->
        <item name="windowMinWidthMinor">90%</item>

        <!-- 取消标题栏，如果在代码中settitle的话会无效 -->
        <item name="android:windowNoTitle">true</item>

        <!-- 标题的和Message的文字颜色 -->
        <!--<item name="android:textColorPrimary">@color/black</item>-->

        <!-- 修改顶部标题背景颜色，具体颜色自己定，可以是图片 -->
        <item name="android:topDark">@color/app_main_color_deep</item>

        <!--<item name="android:background">@color/white</item>-->

        <!-- 在某些系统上面设置背景颜色之后出现奇怪的背景，处这里设置背景为透明，为了隐藏边框 -->
        <!--<item name="android:windowBackground">@android:color/transparent</item>-->
        <!--<item name="android:windowFrame">@null</item>-->

        <!-- 进入和退出动画，左进右出（系统自带） -->
        <!--<item name="android:windowAnimationStyle">@android:style/Animation.Translucent</item>-->

        <!-- 按钮字体颜色,全部一起改，单个改需要在Java代码中修改 -->
        <item name="colorAccent">@color/app_main_color</item>
    </style>
```
我已经打上了详细的注释，相信应该很容易理解

##### 2、写一个接口，用于 DialogFragmentHelper 与逻辑层之间进行数据监听
```
public interface IDialogResultListener<T> {
    void onDataResult(T result);
}
```
准备工作做完了，就让我们开工撸 DialogFragmentHelper 吧，因为篇幅有限，我只是代表性的选了其中的一些效果来讲，具体的代码，可以参考下 **[DialogFragmentDemos](https://github.com/developerHaoz/DialogFragmentDemos)**

```
public class DialogFragmentHelper {

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
     * 带输入框的弹出窗
     */
    private static final int INSERT_THEME = R.style.Base_AlertDialog;
    private static final String INSERT_TAG  = TAG_HEAD + ":insert";

    public static void showInsertDialog(FragmentManager manager, final String title, final IDialogResultListener<String> resultListener, final boolean cancelable){

        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(new CommonDialogFragment.OnCallDialog() {
            @Override
            public Dialog getDialog(Context context) {
                // ...
                AlertDialog.Builder builder = new AlertDialog.Builder(context, INSERT_THEME);
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
}
```
可以看到因为我们实现封装了 CommonFragment，所有这些效果的实现都变得相当的简单吗，这便是封装给我们带来的便利和好处。

就以 `加载中的弹出窗` 为例，来看看我们是怎么实现的
```
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
```
我们先调用了 CommonDialogFragment  的构造函数，将一个 ProgressDialog 传进去，然后依次传入 cancelable 和 cancelListener，最后调用 show() 函数，将DialogFragment 显示出来，因为我们使用了构造函数的重载，可以看到最简单的构造函数只需要传入两个参数就行了，是不是相当的简洁啊。

应该还没忘了我们上面创建了一个 `IDialogResultListener<T>` 用于 DialogFragment 与逻辑层之间进行数据监听吧，为了能传入各种各样类型的数据，这里我使用了 `泛型` 来进行处理，就以 `带输入框的弹出窗` 为例来看看究竟要怎么使用吧

```
    public static void showInsertDialog(FragmentManager manager, final String title, final IDialogResultListener<String> resultListener, final boolean cancelable){

        CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(new CommonDialogFragment.OnCallDialog() {

            @Override
            public Dialog getDialog(Context context) {

             // ... 这里省略一部分代码
                AlertDialog.Builder builder = new AlertDialog.Builder(context, INSERT_THEME);
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
```
可以看到我们在 `showInsertDialog()` 方法中传入了`IDialogResultListener<String> resultListener`，当我们想要处理输入的内容的时候，只要在外部调用的时候，new 一个IDialogResultListener 传进去，然后实现 onDataResult() 方法就行了

以上便是全文的内容，具体的代码以及示例我都放上 Github 了，有需要的朋友可以去看一下 **[DialogFragmentDemos](https://github.com/developerHaoz/DialogFragmentDemos)**，如果觉得对你有所帮助的话，就赏个 star 吧！

-----
### 猜你喜欢
- [手把手教你从零开始做一个好看的 APP](http://www.jianshu.com/p/8d2d74d6046f)
- [Android 能让你少走弯路的干货整理](http://www.jianshu.com/p/514656c383a2)
- [Android 一款十分简洁、优雅的日记 APP](http://www.jianshu.com/p/b4fde6b835a3)
