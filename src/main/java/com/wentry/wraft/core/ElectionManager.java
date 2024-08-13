package com.wentry.wraft.core;

import com.google.common.collect.Lists;
import com.wentry.wraft.transport.packet.VoteReqPacket;
import com.wentry.wraft.pojo.VoteResult;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 选举及状态维护
 */
public class ElectionManager {

    private static final Logger log = LoggerFactory.getLogger(ElectionManager.class);

    private static final Set<Integer> votedTerm = new HashSet<>();

    /**
     * 发起选举
     */
    public static void doElection() {
        if (!StateManager.compareAndSet(
                Lists.newArrayList(NodeStats.FOLLOWER, NodeStats.CANDIDATE),
                NodeStats.CANDIDATE)
        ) {
            log.debug("set state to CANDIDATE fail, for curr state is :{}", StateManager.currState());
            return;
        }
        int currTerm = StateManager.getTerm().incrementAndGet();
        log.debug("begin to election for term:{}", currTerm);
        initVote(currTerm, ClusterManager.nodes.entrySet().size() + 1);
        //先投自己一票
        rcvVote(currTerm);
        for (Map.Entry<String, Channel> ety : ClusterManager.nodes.entrySet()) {
            ety.getValue().writeAndFlush(new VoteReqPacket()
                    .setTerm(currTerm)
                    .setCandidateId(ClusterManager.localId())
            );
        }
    }

    /**
     * 接受选票并统计结果
     */
    public static void rcvVote(int term) {

        if (StateManager.getTerm().get() > term) {
            //当前的term，已经大于term，则不处理投票了
            log.debug("return for curr term gt vote term, curr:{},voteTerm:{}", StateManager.getTerm(), term);
            return;
        }

        //给自己投一票先
        ElectionManager.recordVoteTerm(term);
        VoteResult voteResult = voteResultMap.get(term);
        if (voteResult == null) {
            log.debug("voteResult null for term:{}", term);
            return;
        }
        if (voteResult.isFinished()) {
            log.debug("vote is finished, voteTerm:{}", term);
            return;
        }
        if (voteResult.getVoted().addAndGet(1) > voteResult.getTotal() / 2
                && StateManager.compareAndSet(NodeStats.CANDIDATE, NodeStats.LEADER)
        ) {
            log.info("candidate success for term:{},voteResult:{}", term, voteResult);
            //过半则当选 term 的leader，发送通知
            voteResult.setFinished(true);
            ClusterManager.setLeader(ClusterManager.localId());
            Scheduler.getInstance().heartBeatCountdown(term);
        }
    }

    static Map<Integer, VoteResult> voteResultMap = new ConcurrentHashMap<>();

    /**
     * 初始化投票结果统计
     */
    private static void initVote(int term, int total) {
        voteResultMap.put(term, new VoteResult().setTotal(total));
    }

    public static void recordVoteTerm(int votedTerm) {
        ElectionManager.votedTerm.add(votedTerm);
    }

    public static boolean termVoted(int term) {
        return votedTerm.contains(term);
    }
}
