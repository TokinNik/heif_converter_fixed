import 'heif_converter_platform_interface.dart';

class HeifConverter {
  static Future<String?> convert(
    String path, {
    String? output,
    String? format,
    int? quality,
  }) {
    return HeifConverterPlatform.instance.convert(
      path,
      output: output,
      format: format,
      quality: quality,
    );
  }
}
