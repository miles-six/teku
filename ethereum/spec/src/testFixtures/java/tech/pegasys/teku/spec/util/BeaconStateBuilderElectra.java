/*
 * Copyright Consensys Software Inc., 2022
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package tech.pegasys.teku.spec.util;

import static com.google.common.base.Preconditions.checkNotNull;

import tech.pegasys.teku.infrastructure.ssz.SszList;
import tech.pegasys.teku.infrastructure.ssz.collections.SszUInt64List;
import tech.pegasys.teku.infrastructure.ssz.primitive.SszByte;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;
import tech.pegasys.teku.spec.Spec;
import tech.pegasys.teku.spec.SpecMilestone;
import tech.pegasys.teku.spec.SpecVersion;
import tech.pegasys.teku.spec.datastructures.execution.ExecutionPayloadHeader;
import tech.pegasys.teku.spec.datastructures.state.SyncCommittee;
import tech.pegasys.teku.spec.datastructures.state.beaconstate.versions.electra.BeaconStateElectra;
import tech.pegasys.teku.spec.datastructures.state.beaconstate.versions.electra.BeaconStateSchemaElectra;
import tech.pegasys.teku.spec.datastructures.state.beaconstate.versions.electra.MutableBeaconStateElectra;

public class BeaconStateBuilderElectra
    extends AbstractBeaconStateBuilder<
        BeaconStateElectra, MutableBeaconStateElectra, BeaconStateBuilderElectra> {
  private UInt64 nextWithdrawalIndex;
  private UInt64 nextWithdrawalValidatorIndex;

  private SszList<SszByte> previousEpochParticipation;
  private SszList<SszByte> currentEpochParticipation;
  private SszUInt64List inactivityScores;
  private SyncCommittee currentSyncCommittee;
  private SyncCommittee nextSyncCommittee;
  private ExecutionPayloadHeader latestExecutionPayloadHeader;

  protected BeaconStateBuilderElectra(
      final SpecVersion spec,
      final DataStructureUtil dataStructureUtil,
      final int defaultValidatorCount,
      final int defaultItemsInSSZLists) {
    super(spec, dataStructureUtil, defaultValidatorCount, defaultItemsInSSZLists);
  }

  @Override
  protected BeaconStateElectra getEmptyState() {
    return BeaconStateSchemaElectra.create(spec.getConfig()).createEmpty();
  }

  @Override
  protected void setUniqueFields(final MutableBeaconStateElectra state) {
    state.setPreviousEpochParticipation(previousEpochParticipation);
    state.setCurrentEpochParticipation(currentEpochParticipation);
    state.setInactivityScores(inactivityScores);
    state.setCurrentSyncCommittee(currentSyncCommittee);
    state.setNextSyncCommittee(nextSyncCommittee);
    state.setLatestExecutionPayloadHeader(latestExecutionPayloadHeader);
    state.setNextWithdrawalIndex(nextWithdrawalIndex);
    state.setNextWithdrawalValidatorIndex(nextWithdrawalValidatorIndex);
  }

  public static BeaconStateBuilderElectra create(
      final DataStructureUtil dataStructureUtil,
      final Spec spec,
      final int defaultValidatorCount,
      final int defaultItemsInSSZLists) {
    return new BeaconStateBuilderElectra(
        spec.forMilestone(SpecMilestone.ELECTRA),
        dataStructureUtil,
        defaultValidatorCount,
        defaultItemsInSSZLists);
  }

  public BeaconStateBuilderElectra nextWithdrawalIndex(final UInt64 nextWithdrawalIndex) {
    checkNotNull(nextWithdrawalIndex);
    this.nextWithdrawalIndex = nextWithdrawalIndex;
    return this;
  }

  public BeaconStateBuilderElectra nextWithdrawalValidatorIndex(
      final UInt64 nextWithdrawalValidatorIndex) {
    checkNotNull(nextWithdrawalValidatorIndex);
    this.nextWithdrawalValidatorIndex = nextWithdrawalValidatorIndex;
    return this;
  }

  private BeaconStateSchemaElectra getBeaconStateSchema() {
    return (BeaconStateSchemaElectra) spec.getSchemaDefinitions().getBeaconStateSchema();
  }

  @Override
  protected void initDefaults() {
    super.initDefaults();

    final BeaconStateSchemaElectra schema = getBeaconStateSchema();

    previousEpochParticipation =
        dataStructureUtil.randomSszList(
            schema.getPreviousEpochParticipationSchema(),
            defaultValidatorCount,
            dataStructureUtil::randomSszByte);
    currentEpochParticipation =
        dataStructureUtil.randomSszList(
            schema.getCurrentEpochParticipationSchema(),
            defaultValidatorCount,
            dataStructureUtil::randomSszByte);
    inactivityScores =
        dataStructureUtil.randomSszUInt64List(
            schema.getInactivityScoresSchema(), defaultItemsInSSZLists);
    currentSyncCommittee = dataStructureUtil.randomSyncCommittee();
    nextSyncCommittee = dataStructureUtil.randomSyncCommittee();
    latestExecutionPayloadHeader =
        dataStructureUtil.randomExecutionPayloadHeader(
            dataStructureUtil.getSpec().forMilestone(SpecMilestone.ELECTRA));

    this.nextWithdrawalIndex = UInt64.ZERO;
    this.nextWithdrawalValidatorIndex =
        defaultValidatorCount > 0
            ? dataStructureUtil.randomUInt64(defaultValidatorCount)
            : UInt64.ZERO;
  }
}
