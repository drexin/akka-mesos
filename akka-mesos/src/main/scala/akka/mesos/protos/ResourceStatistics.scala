package akka.mesos.protos

import org.apache.mesos.Protos

import scala.concurrent.duration._
import scala.util.Try

final case class ResourceStatistics(
    cpu: ResourceStatistics.CPU,
    memory: ResourceStatistics.Memory,
    perf: PerfStatistics,
    network: ResourceStatistics.Network) extends ProtoWrapper[Protos.ResourceStatistics] {

  def toProto: Protos.ResourceStatistics =
    Protos.ResourceStatistics
      .newBuilder
      .mergeFrom(cpu.toProto)
      .mergeFrom(memory.toProto)
      .setPerf(perf.toProto)
      .mergeFrom(network.toProto)
      .build()
}

object ResourceStatistics extends ProtoReads[ResourceStatistics] {
  final case class CPU(
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

  object CPU {
    def apply(proto: Protos.ResourceStatistics): CPU = {
      CPU(
        if (proto.hasCpusUserTimeSecs) Some(proto.getCpusUserTimeSecs.seconds) else None,
        if (proto.hasCpusSystemTimeSecs) Some(proto.getCpusSystemTimeSecs.seconds) else None,
        if (proto.hasCpusThrottledTimeSecs) Some(proto.getCpusThrottledTimeSecs.seconds) else None,
        if (proto.hasCpusLimit) Some(proto.getCpusLimit) else None,
        if (proto.hasCpusNrPeriods) Some(proto.getCpusNrPeriods) else None,
        if (proto.hasCpusNrThrottled) Some(proto.getCpusNrThrottled) else None
      )
    }
  }

  final case class Memory(
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

  object Memory {
    def apply(proto: Protos.ResourceStatistics): Memory = {
      Memory(
        if (proto.hasMemRssBytes) Some(proto.getMemRssBytes) else None,
        if (proto.hasMemLimitBytes) Some(proto.getMemLimitBytes) else None,
        if (proto.hasMemFileBytes) Some(proto.getMemFileBytes) else None,
        if (proto.hasMemAnonBytes) Some(proto.getMemAnonBytes) else None,
        if (proto.hasMemMappedFileBytes) Some(proto.getMemMappedFileBytes) else None
      )
    }
  }

  final case class Network(
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

  object Network {
    def apply(proto: Protos.ResourceStatistics): Network = {
      Network(
        if (proto.hasNetRxPackets) Some(proto.getNetRxPackets) else None,
        if (proto.hasNetRxBytes) Some(proto.getNetRxBytes) else None,
        if (proto.hasNetRxErrors) Some(proto.getNetRxDropped) else None,
        if (proto.hasNetRxDropped) Some(proto.getNetRxDropped) else None,
        if (proto.hasNetTxPackets) Some(proto.getNetTxPackets) else None,
        if (proto.hasNetTxBytes) Some(proto.getNetTxBytes) else None,
        if (proto.hasNetTxErrors) Some(proto.getNetTxErrors) else None,
        if (proto.hasNetTxDropped) Some(proto.getNetTxDropped) else None
      )
    }
  }

  override def fromBytes(bytes: Array[Byte]): Try[ResourceStatistics] = Try {
    ResourceStatistics(Protos.ResourceStatistics.parseFrom(bytes))
  }

  def apply(proto: Protos.ResourceStatistics): ResourceStatistics = {
    ResourceStatistics(
      CPU(proto),
      Memory(proto),
      PerfStatistics(proto.getPerf),
      Network(proto)
    )
  }
}
