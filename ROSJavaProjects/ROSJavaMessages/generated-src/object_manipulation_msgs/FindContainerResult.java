package object_manipulation_msgs;

public interface FindContainerResult extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "object_manipulation_msgs/FindContainerResult";
  static final java.lang.String _DEFINITION = "# ====== DO NOT MODIFY! AUTOGENERATED FROM AN ACTION DEFINITION ======\n# refined pose and dimensions of bounding box for container\ngeometry_msgs/PoseStamped box_pose\ngeometry_msgs/Vector3     box_dims\n\n# cloud chunks of stuff in container, and container\nsensor_msgs/PointCloud2   contents\nsensor_msgs/PointCloud2   container\nsensor_msgs/PointCloud2[] clusters\n\n";
  geometry_msgs.PoseStamped getBoxPose();
  void setBoxPose(geometry_msgs.PoseStamped value);
  geometry_msgs.Vector3 getBoxDims();
  void setBoxDims(geometry_msgs.Vector3 value);
  sensor_msgs.PointCloud2 getContents();
  void setContents(sensor_msgs.PointCloud2 value);
  sensor_msgs.PointCloud2 getContainer();
  void setContainer(sensor_msgs.PointCloud2 value);
  java.util.List<sensor_msgs.PointCloud2> getClusters();
  void setClusters(java.util.List<sensor_msgs.PointCloud2> value);
}
