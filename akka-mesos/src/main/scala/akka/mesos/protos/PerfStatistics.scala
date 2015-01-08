package akka.mesos.protos

import org.apache.mesos.Protos

final case class PerfStatistics(
    timestamp: Double,
    duration: Double,
    hardware: PerfStatistics.Hardware,
    software: PerfStatistics.Software,
    cache: PerfStatistics.Cache) extends ProtoWrapper[Protos.PerfStatistics] {
  def toProto: Protos.PerfStatistics = {
    Protos.PerfStatistics
      .newBuilder
      .setTimestamp(timestamp)
      .setDuration(duration)
      .mergeFrom(hardware.toProto)
      .mergeFrom(software.toProto)
      .mergeFrom(cache.toProto)
      .build()
  }
}

object PerfStatistics {
  final case class Hardware(
      cycles: Option[Long] = None,
      stalledCyclesFrontend: Option[Long] = None,
      stalledCyclesBackend: Option[Long] = None,
      instructions: Option[Long] = None,
      cacheReferences: Option[Long] = None,
      cacheMisses: Option[Long] = None,
      branches: Option[Long] = None,
      branchMisses: Option[Long] = None,
      busCycles: Option[Long] = None,
      refCycles: Option[Long] = None) {
    def toProto: Protos.PerfStatistics = {
      val builder = Protos.PerfStatistics.newBuilder

      cycles.foreach(builder.setCycles)
      stalledCyclesFrontend.foreach(builder.setStalledCyclesFrontend)
      stalledCyclesBackend.foreach(builder.setStalledCyclesBackend)
      instructions.foreach(builder.setInstructions)
      cacheReferences.foreach(builder.setCacheReferences)
      cacheMisses.foreach(builder.setCacheMisses)
      branches.foreach(builder.setBranches)
      branchMisses.foreach(builder.setBranchMisses)
      busCycles.foreach(builder.setBusCycles)
      refCycles.foreach(builder.setRefCycles)

      builder.build()
    }
  }

  final case class Software(
      cpuClock: Option[Double] = None,
      taskClock: Option[Double] = None,
      pageFaults: Option[Long] = None,
      minorFaults: Option[Long] = None,
      majorFaults: Option[Long] = None,
      contextSwitches: Option[Long] = None,
      cpuMigrations: Option[Long] = None,
      alignmentFaults: Option[Long] = None,
      emulationFaults: Option[Long] = None) {
    def toProto: Protos.PerfStatistics = {
      val builder = Protos.PerfStatistics.newBuilder

      cpuClock.foreach(builder.setCpuClock)
      taskClock.foreach(builder.setTaskClock)
      pageFaults.foreach(builder.setPageFaults)
      minorFaults.foreach(builder.setMinorFaults)
      majorFaults.foreach(builder.setMajorFaults)
      contextSwitches.foreach(builder.setContextSwitches)
      cpuMigrations.foreach(builder.setCpuMigrations)
      alignmentFaults.foreach(builder.setAlignmentFaults)
      emulationFaults.foreach(builder.setEmulationFaults)

      builder.build()
    }
  }

  final case class Cache(
      l1DCacheLoads: Option[Long] = None,
      l1DCacheLoadMisses: Option[Long] = None,
      l1DCacheStores: Option[Long] = None,
      l1DCacheStoreMisses: Option[Long] = None,
      l1DCachePrefetches: Option[Long] = None,
      l1DCachePrefetchMisses: Option[Long] = None,
      l1ICacheLoads: Option[Long] = None,
      l1ICacheLoadMisses: Option[Long] = None,
      l1ICachePrefetches: Option[Long] = None,
      l1ICachePrefetchMisses: Option[Long] = None,
      llcLoads: Option[Long] = None,
      llcLoadMisses: Option[Long] = None,
      llcStores: Option[Long] = None,
      llcStoreMisses: Option[Long] = None,
      llcPrefetches: Option[Long] = None,
      llcPrefetchMisses: Option[Long] = None,
      dtlbLoads: Option[Long] = None,
      dtlbLoadMisses: Option[Long] = None,
      dtlbStores: Option[Long] = None,
      dtlbStoreMisses: Option[Long] = None,
      dtlbPrefetches: Option[Long] = None,
      dtlbPrefetchMisses: Option[Long] = None,
      itlbLoads: Option[Long] = None,
      itlbLoadMisses: Option[Long] = None,
      branchLoads: Option[Long] = None,
      branchLoadMisses: Option[Long] = None,
      nodeLoads: Option[Long] = None,
      nodeLoadMisses: Option[Long] = None,
      nodeStores: Option[Long] = None,
      nodeStoreMisses: Option[Long] = None,
      nodePrefetches: Option[Long] = None,
      nodePrefetchMisses: Option[Long] = None) {
    def toProto: Protos.PerfStatistics = {
      val builder = Protos.PerfStatistics.newBuilder

      l1DCacheLoads.foreach(builder.setL1DcacheLoads)
      l1DCacheLoadMisses.foreach(builder.setL1DcacheLoadMisses)
      l1DCacheStores.foreach(builder.setL1DcacheStores)
      l1DCacheStoreMisses.foreach(builder.setL1DcacheStoreMisses)
      l1DCachePrefetches.foreach(builder.setL1DcachePrefetchMisses)
      l1DCachePrefetchMisses.foreach(builder.setL1DcachePrefetchMisses)
      l1ICacheLoads.foreach(builder.setL1IcacheLoads)
      l1ICacheLoadMisses.foreach(builder.setL1IcacheLoadMisses)
      l1ICachePrefetches.foreach(builder.setL1IcachePrefetches)
      l1ICachePrefetchMisses.foreach(builder.setL1IcachePrefetchMisses)
      llcLoads.foreach(builder.setLlcLoads)
      llcLoadMisses.foreach(builder.setLlcLoadMisses)
      llcStores.foreach(builder.setLlcStores)
      llcStoreMisses.foreach(builder.setLlcStoreMisses)
      llcPrefetches.foreach(builder.setLlcPrefetches)
      llcPrefetchMisses.foreach(builder.setLlcPrefetchMisses)
      dtlbLoads.foreach(builder.setDtlbLoads)
      dtlbLoadMisses.foreach(builder.setDtlbLoadMisses)
      dtlbStores.foreach(builder.setDtlbStores)
      dtlbStoreMisses.foreach(builder.setDtlbStoreMisses)
      dtlbPrefetches.foreach(builder.setDtlbPrefetches)
      dtlbPrefetchMisses.foreach(builder.setDtlbPrefetchMisses)
      itlbLoads.foreach(builder.setItlbLoads)
      itlbLoadMisses.foreach(builder.setItlbLoadMisses)
      branchLoads.foreach(builder.setBranchLoads)
      branchLoadMisses.foreach(builder.setBranchLoadMisses)
      nodeLoads.foreach(builder.setNodeLoads)
      nodeLoadMisses.foreach(builder.setNodeLoadMisses)
      nodeStores.foreach(builder.setNodeStores)
      nodeStoreMisses.foreach(builder.setNodeStoreMisses)
      nodePrefetches.foreach(builder.setNodePrefetches)
      nodePrefetchMisses.foreach(builder.setNodePrefetchMisses)

      builder.build()
    }
  }

  def apply(proto: Protos.PerfStatistics): PerfStatistics = {
    PerfStatistics(proto.getTimestamp, proto.getDuration, Hardware(), Software(), Cache())
  }
}
