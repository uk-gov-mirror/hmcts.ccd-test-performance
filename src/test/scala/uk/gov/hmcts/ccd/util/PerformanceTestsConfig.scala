package uk.gov.hmcts.ccd.util

import com.typesafe.config.ConfigFactory
import uk.gov.hmcts.ccd.config.Implicits.RichConfig
import scala.util.Random

trait PerformanceTestsConfig {

  val config = PerformanceTestsConfig.config

  val UserCcdId = config.getInt("userCcdId")
  val UserImportId = config.getInt("userImportId")
  val UserAuthUrl = config.getString("idamUserTokenUrl")
  val S2SAuthUrl = config.getString("idamS2SUrl")
  val UserProfileUrl = config.getString("userProfileUrl")
  val CaseDataUrl = config.getString("caseDataUrl")
  val CaseDefinitionUrl = config.getString("caseDefinitionUrl")
  val MaxSimulationDuration = config.getInt("maxSimulationDurationMinutes")
  val RequestRateSec = Option(System.getProperty("request.rate")).map(_.toDouble)

  def caseDataUrl(path: String) = config.getString("caseDataUrl") + "/" + path
  def cases: Option[List[String]] = config.getOptionalStringList("cases")

  def pickRandomReference(): String = {
    cases.map(references => {
      Random.shuffle(references).head
    }).getOrElse(
      throw new RuntimeException("'getCaseUrl' contains a placeholder but no 'cases' property has been found")
    )
  }
}

object PerformanceTestsConfig {

  val environment = Option(System.getenv("ENVIRONMENT"))
  val cfg = ConfigFactory.load()

  val config = environment.map {env =>
    println(s"Loading config for environment: $env")
    val envSpecific = cfg.getConfig(env)
    envSpecific.withFallback(cfg)
  }.getOrElse({
    println("Loading default config")
    cfg
  })
}