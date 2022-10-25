package com.github.tesis.runnerms.utils

import org.apache.commons.io.IOUtils
import org.springframework.core.io.ClassPathResource
import java.nio.charset.Charset

// Limit Size File 2 GB
fun getQueryFromFile(resource: String, folderResources: Boolean = true): String {
    val path = if (folderResources) "$resource" else resource
    return IOUtils.toString(ClassPathResource(path).inputStream, Charset.defaultCharset())
}
