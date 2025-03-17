package pl.scalasentinel.collector.twitter.exceptions

class TwitterApiException(
  message: String,
  cause: Throwable = null
) extends Exception(message, cause)
