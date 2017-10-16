package us.ihmc.geometry.polytope;

import java.util.ArrayList;
import java.util.List;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import us.ihmc.euclid.interfaces.GeometryObject;
import us.ihmc.euclid.transform.interfaces.Transform;
import us.ihmc.euclid.tuple3D.Point3D;
import us.ihmc.euclid.tuple3D.Vector3D;
import us.ihmc.euclid.tuple3D.interfaces.Point3DReadOnly;
import us.ihmc.euclid.tuple3D.interfaces.Vector3DReadOnly;

/**
 * This class stores the location of a point which is the vertex of a polytope
 * A list of polytope edges originating from this vertex is also stored for ease of algorithm design 
 * Faces to which this vertex belongs can be accessed by iterating through the list of edges
 * 
 * Based on original class by Jerry Pratt
 * @author Apoorv S
 *
 */
public class ExtendedPolytopeVertex implements GeometryObject<ExtendedPolytopeVertex>, PolytopeVertexBasics
{
   private final Point3D position = new Point3D();
   private final ArrayList<PolytopeHalfEdge> associatedEdges = new ArrayList<>();
   private DenseMatrix64F jacobian;
   
   public ExtendedPolytopeVertex()
   {
      
   }
   
   public ExtendedPolytopeVertex(double x, double y, double z)
   {
      position.set(x, y, z);
   }

   public ExtendedPolytopeVertex(Point3D position)
   {
      this.position.set(position);
   }

   public ExtendedPolytopeVertex(ExtendedPolytopeVertex vertex)
   {
      this.position.set(vertex.position);
      copyEdges(vertex.getAssociatedEdges());
   }

   @Override
   public void set(ExtendedPolytopeVertex vertex)
   {
      this.position.set(vertex.position);
      clearAssociatedEdgeList();
      copyEdges(vertex.getAssociatedEdges());
   }

   public List<? extends PolytopeHalfEdge> getAssociatedEdges()
   {
      return associatedEdges;
   }
   
   public PolytopeHalfEdge getAssociatedEdge(int index)
   {
      return associatedEdges.get(index);
   }
   
   public void removeAssociatedEdge(PolytopeHalfEdge edgeToAdd)
   {
      associatedEdges.remove(edgeToAdd);
   }
   
   public void clearAssociatedEdgeList()
   {
      associatedEdges.clear();
   }

   public void copyEdges(List<? extends PolytopeHalfEdge> edgeList)
   {
      for(int i = 0; i < edgeList.size(); i++)
      {
         addAssociatedEdge(edgeList.get(i));
      }
   }

   public void addAssociatedEdge(PolytopeHalfEdge edge)
   {
      if (!isAssociatedWithEdge(edge))
         associatedEdges.add(edge);
   }
   
   public boolean isAssociatedWithEdge(PolytopeHalfEdge edgeToCheck)
   {
      return associatedEdges.contains(edgeToCheck);
   }

   public boolean isAssociatedWithEdge(PolytopeHalfEdge edgeToCheck, double epsilon)
   {
      boolean result = associatedEdges.size() > 0;
      for(int i = 0; result && i < associatedEdges.size(); i++)
      {
         result &= associatedEdges.get(i).epsilonEquals(edgeToCheck, epsilon);
      }
      return result;
   }

   public int getNumberOfAssociatedEdges()
   {
      return associatedEdges.size();
   }

   public Point3D getPosition()
   {
      return position;
   }

   public double dot(Vector3DReadOnly vector)
   {
      return position.getX() * vector.getX() + position.getY() * vector.getY() + position.getZ() * vector.getZ();
   }

   public String toString()
   {
      return "{" + position.getX() + ", " + position.getY() + ", " + position.getZ() + "}";
   }

   public double getX()
   {
      return position.getX();
   }

   public double getY()
   {
      return position.getY();
   }

   public double getZ()
   {
      return position.getZ();
   }
   
   public double getElement(int index)
   {
      return position.getElement(index);
   }

   @Override
   public void applyTransform(Transform transform)
   {
      transform.transform(position);
   }

   @Override
   public void applyInverseTransform(Transform transform)
   {
      transform.inverseTransform(position);
   }

   public boolean isAnyFaceMarked()
   {
      boolean isMarked = false;
      for(int i = 0; !isMarked && i < associatedEdges.size(); i++)
      {
         isMarked |= associatedEdges.get(i).getFace().isMarked();
      }
      //PrintTools.debug(toString() + " " +isMarked);
      return isMarked;
   }
   
   @Override
   public boolean epsilonEquals(ExtendedPolytopeVertex other, double epsilon)
   {
      return position.epsilonEquals(other.position, epsilon);
   }

   @Override
   public boolean containsNaN()
   {
      return position.containsNaN();
   }

   @Override
   public void setToNaN()
   {
      position.setToNaN();
   }

   @Override
   public void setToZero()
   {
      position.setToZero();
   }

   @Override
   public void setX(double x)
   {
      position.setX(x);
   }

   @Override
   public void setY(double y)
   {
      position.setY(y);
   }

   @Override
   public void setZ(double z)
   {
      position.setZ(z);
   }

   @Override
   public double getShortestDistanceTo(Point3DReadOnly point)
   {
      double dx = getX() - point.getX();
      double dy = getX() - point.getX();
      double dz = getX() - point.getX();
      return Math.sqrt( (dx * dx) + (dy * dy) + (dz * dz));
   }

   @Override
   public void getSupportVectorDirectionTo(Point3DReadOnly point, Vector3D supportVectorToPack)
   {
      supportVectorToPack.sub(point, this);
   }

   @Override
   public void getSupportVectorJacobianTo(Point3DReadOnly point, DenseMatrix64F jacobianToPack)
   {
      jacobianToPack.set(jacobian);
      CommonOps.scale(-1.0, jacobianToPack);
   }

   public void setJacobian(DenseMatrix64F jacobian)
   {
      this.jacobian.set(jacobian);
   }
   
   public DenseMatrix64F getJacobian()
   {
      return jacobian;
   }
   
   @Override
   public Simplex getSmallestSimplexMemberReference(Point3DReadOnly point)
   {
      return this;
   }
}