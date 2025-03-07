package com.joutvhu.heif_converter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** HeifConverterPlugin */
public class HeifConverterPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Context context = null;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "heif_converter");
    channel.setMethodCallHandler(this);
    context = flutterPluginBinding.getApplicationContext();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("convert")) {
      String path = call.hasArgument("path") ? call.argument("path") : null;
      String output = call.hasArgument("output") ? call.argument("output") : null;
      String format = call.hasArgument("format") ? call.argument("format") : null;
      String quality = call.hasArgument("quality") ? call.argument("quality") : null;
      if (path == null || path.isEmpty()) {
        result.error("illegalArgument", "Input path is blank.", null);
        return;
      }
      if (output == null || output.isEmpty()) {
        if (format != null && context != null) {
          output = MessageFormat.format("{0}/{1}.{2}",
                  context.getCacheDir(), System.currentTimeMillis(), format);
        } else {
          result.error("illegalArgument", "Output path and format is blank.", null);
          return;
        }
      }
      try {
        output = convert(path, output, quality);
        result.success(output);
      } catch (Exception e) {
        result.error("conversionFailed", e.getMessage(), e);
      }
    } else {
      result.notImplemented();
    }
  }

  private String convert(String path, String output, int quality) throws IOException {
    Bitmap bitmap = BitmapFactory.decodeFile(path);
    File file = new File(output);
    file.createNewFile();
    Bitmap.CompressFormat format = getFormat(output);
    int q = 100
    if(quality != null){
        q = quality
    }
    bitmap.compress(format, q, new FileOutputStream(file));
    return file.getPath();
  }

  private Bitmap.CompressFormat getFormat(String path) {
    if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
      return Bitmap.CompressFormat.JPEG;
    return Bitmap.CompressFormat.PNG;
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
