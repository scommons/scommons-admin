package scommons.admin.domain

import com.typesafe.config.Config
import io.getquill.{PostgresAsyncContext, SnakeCase}

class AdminDBContext(config: Config) extends PostgresAsyncContext(SnakeCase, config)
