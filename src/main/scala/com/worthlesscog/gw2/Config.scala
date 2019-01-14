package com.worthlesscog.gw2

import java.nio.file.{Files, Path}

import Utils.{noneOrString, ois, saveObject, using}

case class SavedConfig(var token: Option[String])

class Config(path: Path) extends Mappable {

    private lazy val configFile = path.resolve(SETTINGS)
    private lazy val config = load()

    private lazy val defaultConfig = SavedConfig(token = None)

    def token = config.token
    def token_=(t: String) = {
        config.token = Some(t)
        save
    }

    private def load(): SavedConfig =
        if (Files.exists(configFile))
            using(configFile |> ois) { _.readObject.asInstanceOf[SavedConfig] }
        else
            defaultConfig |> saveObject(configFile)

    private def save(): Unit =
        config |> saveObject(configFile)

    def toMap() = Map(
        "token" -> noneOrString(token))

}
