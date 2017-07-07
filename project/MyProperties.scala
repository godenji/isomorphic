trait MyProperties extends meta.Properties with Properties {
  override val appName = "value-class-isomorphism"
  val appVersion = valueClassBindableVersion
}
