package pr2_common_action_msgs;

public interface ArmMoveIKActionFeedback extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "pr2_common_action_msgs/ArmMoveIKActionFeedback";
  static final java.lang.String _DEFINITION = "# ====== DO NOT MODIFY! AUTOGENERATED FROM AN ACTION DEFINITION ======\n\nHeader header\nactionlib_msgs/GoalStatus status\nArmMoveIKFeedback feedback\n";
  std_msgs.Header getHeader();
  void setHeader(std_msgs.Header value);
  actionlib_msgs.GoalStatus getStatus();
  void setStatus(actionlib_msgs.GoalStatus value);
  pr2_common_action_msgs.ArmMoveIKFeedback getFeedback();
  void setFeedback(pr2_common_action_msgs.ArmMoveIKFeedback value);
}
