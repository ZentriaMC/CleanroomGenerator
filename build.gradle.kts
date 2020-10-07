plugins {
    java
    id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
}

group = "eu.mikroskeem.zentria"
version = "2.0.3-SNAPSHOT"

val paperApiVersion = "1.16.3-R0.1-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://papermc.io/repo/repository/maven-public")
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:$paperApiVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

bukkit {
    name = "CleanroomGenerator"
    main = "io.nv.bukkit.CleanroomGenerator.CleanroomGenerator"
    authors = listOf("NVX (Neo_Vortex)", "mikroskeem")
    description = "Custom world generator to generate flat clean rooms. y==0 is bedrock layer=height,block[:datavalue] generator id=layer[|layer[|...]] - Prefix . at the start of the ID to skip bedrock generation at layer0"
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
    website = "https://github.com/ZentriaMC/CleanroomGenerator"
    apiVersion = "1.16"
}