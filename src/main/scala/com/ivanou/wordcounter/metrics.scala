package com.ivanou.wordcounter

import java.lang.management.ManagementFactory
import javax.management.ObjectName

object metrics {

  trait CounterMetricsMBean {
    def wordsCount: Int
  }

  class CounterMetrics(counter: Counter) extends CounterMetricsMBean {
    override def wordsCount: Int = counter.total
  }

  def withJmxMetrics(counter: => Counter): Counter = {
    val c = counter
    val server = ManagementFactory.getPlatformMBeanServer
    server.registerMBean(new CounterMetrics(c), ObjectName.getInstance(s"com.ivanou.wordcounter:name=WordCounter"))
    c
  }
}
