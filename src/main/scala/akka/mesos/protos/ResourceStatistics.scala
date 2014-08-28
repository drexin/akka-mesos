package akka.mesos.protos

import org.apache.mesos.Protos

import scala.concurrent.duration.Duration

case class ResourceStatistics(
    cpu: ResourceStatistics.CPU,
    memory: ResourceStatistics.Memory,
    perf: PerfStatistics,
    network: ResourceStatistics.Network) {

  def toProto: Protos.ResourceStatistics =
    Protos.ResourceStatistics
      .newBuilder
      .mergeFrom(cpu.toProto)
      .mergeFrom(memory.toProto)
      .setPerf(perf.toProto)
      .mergeFrom(network.toProto)
      .build()
}

object ResourceStatistics {
  case class CPU(
      userTime: Option[Duration] = None,
      systemTime: Option[Duration] = None,
      throttledTime: Option[Duration] = None,
      limit: Option[Double] = None,
      nrPeriods: Option[Int] = None,
      nrThrottled: Option[Int] = None) {
    def toProto: Protos.ResourceStatistics = {
      val builder = Protos.ResourceStatistics.newBuilder

      userTime.foreach(x => builder.setCpusUserTimeSecs(durationToSeconds(x)))
      systemTime.foreach(x => builder.setCpusSystemTimeSecs(durationToSeconds(x)))
      throttledTime.foreach(x => builder.setCpusThrottledTimeSecs(durationToSeconds(x)))
      limit.foreach(builder.setCpusLimit)
      nrPeriods.foreach(builder.setCpusNrPeriods)
      nrThrottled.foreach(builder.setCpusNrThrottled)

      builder.build()
    }

    private def durationToSeconds(d: Duration): Double = d.toMillis / 1000
  }

  case class Memory(
      residentSetSize: Option[Long] = None,
      limit: Option[Long] = None,
      files: Option[Long] = None,
      anonymous: Option[Long] = None,
      memoryMappedFiles: Option[Long] = None) {
    def toProto: Protos.ResourceStatistics = {
      val builder = Protos.ResourceStatistics.newBuilder

      residentSetSize.foreach(builder.setMemRssBytes)
      limit.foreach(builder.setMemLimitBytes)
      files.foreach(builder.setMemFileBytes)
      anonymous.foreach(builder.setMemAnonBytes)
      memoryMappedFiles.foreach(builder.setMemMappedFileBytes)

      builder.build()
    }
  }

  case class Network(
      rxPackets: Option[Long],
      rxBytes: Option[Long],
      rxErrors: Option[Long],
      rxDropped: Option[Long],
      txPackets: Option[Long],
      txBytes: Option[Long],
      txErrors: Option[Long],
      txDropped: Option[Long]) {
    def toProto: Protos.ResourceStatistics = {
      val builder = Protos.ResourceStatistics.newBuilder

      rxPackets.foreach(builder.setNetRxPackets)
      rxBytes.foreach(builder.setNetRxBytes)
      rxErrors.foreach(builder.setNetRxErrors)
      rxDropped.foreach(builder.setNetRxDropped)
      txPackets.foreach(builder.setNetTxPackets)
      txBytes.foreach(builder.setNetTxBytes)
      txErrors.foreach(builder.setNetTxErrors)
      txDropped.foreach(builder.setNetTxDropped)

      builder.build()
    }
  }
}
