package akka.mesos.protos

import org.apache.mesos.Protos

import scala.concurrent.duration.Duration

/*
required double timestamp = 1; // Snapshot time, in seconds since the Epoch.

  // CPU Usage Information:
  // Total CPU time spent in user mode, and kernel mode.
  optional double cpus_user_time_secs = 2;
  optional double cpus_system_time_secs = 3;

  // Number of CPUs allocated.
  optional double cpus_limit = 4;

  // cpu.stat on process throttling (for contention issues).
  optional uint32 cpus_nr_periods = 7;
  optional uint32 cpus_nr_throttled = 8;
  optional double cpus_throttled_time_secs = 9;

  // Memory Usage Information:
  optional uint64 mem_rss_bytes = 5; // Resident Set Size.

  // Amount of memory resources allocated.
  optional uint64 mem_limit_bytes = 6;

  // Broken out memory usage information (files, anonymous, and mmaped files)
  optional uint64 mem_file_bytes = 10;
  optional uint64 mem_anon_bytes = 11;
  optional uint64 mem_mapped_file_bytes = 12;

  // TODO(bmahler): Add disk usage.

  // Perf statistics.
  optional PerfStatistics perf = 13;

  // Network Usage Information:
  optional uint64 net_rx_packets = 14;
  optional uint64 net_rx_bytes = 15;
  optional uint64 net_rx_errors = 16;
  optional uint64 net_rx_dropped = 17;
  optional uint64 net_tx_packets = 18;
  optional uint64 net_tx_bytes = 19;
  optional uint64 net_tx_errors = 20;
  optional uint64 net_tx_dropped = 21;
 */

case class ResourceStatistics(
    cpu: ResourceStatistics.CPU,
    memory: ResourceStatistics.Memory) {

  def toProto: Protos.ResourceStatistics =
    Protos.ResourceStatistics
      .newBuilder
      .mergeFrom(cpu.toProto)
      .mergeFrom(memory.toProto)
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

    private def durationToSeconds(d: Duration): Double = d.toMillis / 1000 / 1000
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
}
