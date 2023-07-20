package games.chinesecheckers.components;

import core.CoreConstants;
import core.components.Component;
import core.interfaces.IComponentContainer;
import core.properties.Property;
import core.properties.PropertyString;
import core.properties.PropertyStringArray;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utilities.Hash;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static core.CoreConstants.imgHash;
import static core.CoreConstants.nameHash;

public class StarBoard extends Component implements IComponentContainer<CCNode> {

    // List of nodes in the board graph
    protected List<CCNode> boardNodes;

    public StarBoard(String name) {
        super(CoreConstants.ComponentType.BOARD, name);
        boardNodes = new ArrayList<>();
    }

    public StarBoard() {
        super(CoreConstants.ComponentType.BOARD);
        boardNodes = new ArrayList<>();
    }

    StarBoard(String name, int ID) {
        super(CoreConstants.ComponentType.BOARD, name, ID);
        boardNodes = new ArrayList<>();
    }

    StarBoard(int ID) {
        super(CoreConstants.ComponentType.BOARD, ID);
        boardNodes = new ArrayList<>();
    }

    /**
     * Copy method, to be implemented by all subclasses.
     * @return - a new instance of this Board, deep copy.
     */
    @Override
    public StarBoard copy() {
        StarBoard b = new StarBoard(componentName, componentID);
        HashMap<Integer, CCNode> nodeCopies = new HashMap<>();
        // Copy board nodes
        for (CCNode bn: boardNodes) {
            CCNode bnCopy = new CCNode(bn.getMaxNeighbours(), "", bn.getComponentID());
            bn.copyComponentTo(bnCopy);

            //Extra CCNode Details!
            ////////////////////////////////////////////////////////////////////////////////////
            // Add Base colour variable
            bnCopy.setColourNode(bn.getBaseColour());

            // Add Peg occupiedPeg variable
            if(bn.isNodeOccupied()){
                bnCopy.setOccupiedPeg((Peg) bn.getOccupiedPeg().copy());
            }

            // Add int X and Y variables
            bnCopy.setCoordinates(bn.getX(), bn.getY());

            // Add Enum Base ?
            /////////////////////////////////////////////////////////////////////////////////////

            nodeCopies.put(bn.getComponentID(), bnCopy);
        }
        // Assign neighbours
        for (CCNode bn: boardNodes) {
            CCNode bnCopy = nodeCopies.get(bn.getComponentID());
//            for (CCNode neighbour: bn.getNeighbours()) {
//                bnCopy.addNeighbour(nodeCopies.get(neighbour.getComponentID()));
//            }
            for (Map.Entry<CCNode, Integer> e: bn.getNeighbourSideMapping().entrySet()) {
                bnCopy.addNeighbour(nodeCopies.get(e.getKey().getComponentID()), e.getValue());
            }
        }
        // Assign new neighbours
        b.setBoardNodes(new ArrayList<>(nodeCopies.values()));
        // Copy properties
        copyComponentTo(b);

        return b;
    }

    /**
     * Returns the node in the list which matches the given property
     * @param prop_id - ID of the property to look for.
     * @param p - Property that has the value to look for.
     * @return - node matching property.
     */
    public CCNode getNodeByProperty(int prop_id, Property p) {
        for (CCNode n : boardNodes) {
            Property prop = n.getProperty(prop_id);
            if(prop != null)
            {
                if(prop.equals(p))
                    return n;
            }
        }
        return null;
    }

    /**
     * Returns the node in the list which matches the given string property
     * @param prop_id - ID of the property to look for.
     * @param value - String value for the property.
     * @return - node matching property
     */
    public CCNode getNodeByStringProperty(int prop_id, String value)
    {
        return getNodeByProperty(prop_id, new PropertyString(value));
    }

    /**
     * @return the list of board nodes
     */
    public List<CCNode> getBoardNodes() {
        return boardNodes;
    }

    /**
     * Returns the node in the list which matches the given ID
     * @param id - ID of node to search for.
     * @return - node matching ID.
     */
    protected CCNode getNodeByID(int id) {
        for (CCNode n : boardNodes) {
            if (n.getComponentID() == id) return n;
        }
        return null;
    }

    /**
     * Sets the list of board nodes to the given list.
     * @param boardNodes - new list of board nodes.
     */
    public void setBoardNodes(List<CCNode> boardNodes) {
        this.boardNodes = boardNodes;
    }

    public void addBoardNode(CCNode bn) {
        this.boardNodes.add(bn);
    }

    public void removeBoardNode(CCNode bn) {
        this.boardNodes.remove(bn);
    }

    public void breakConnection(CCNode bn1, CCNode bn2) {
        bn1.removeNeighbour(bn2);
        bn2.removeNeighbour(bn1);

        // Check if they have at least 1 more neighbour on this board. If not, remove node from this board
        boolean inBoard = false;
        for (CCNode n: bn1.getNeighbours()) {
            if (boardNodes.contains(n)) {
                inBoard = true;
                break;
            }
        }
        if (!inBoard) boardNodes.remove(bn1);

        inBoard = false;
        for (CCNode n: bn2.getNeighbours()) {
            if (boardNodes.contains(n)) {
                inBoard = true;
                break;
            }
        }
        if (!inBoard) boardNodes.remove(bn2);
    }

    public void addConnection(CCNode bn1, CCNode bn2) {
        bn1.addNeighbour(bn2);
        bn2.addNeighbour(bn1);
        if (!boardNodes.contains(bn1)) {
            boardNodes.add(bn1);
        }
        if (!boardNodes.contains(bn2)) {
            boardNodes.add(bn2);
        }
    }

    /**
     * Loads all boards from a JSON file.
     * @param filename - path to file.
     * @return - List of Board objects.
     */
    public static List<StarBoard> loadBoards(String filename)
    {
        JSONParser jsonParser = new JSONParser();
        ArrayList<StarBoard> graphBoards = new ArrayList<>();

        try (FileReader reader = new FileReader(filename)) {

            JSONArray data = (JSONArray) jsonParser.parse(reader);
            for(Object o : data) {
                StarBoard newGraphBoard = new StarBoard();
                newGraphBoard.loadBoard((JSONObject) o);
                graphBoards.add(newGraphBoard);
            }

        }catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return graphBoards;
    }

    /**
     * Loads board nodes from a JSON file.
     * @param board - board to load in JSON format
     */
    public void loadBoard(JSONObject board) {
        componentName = (String) board.get("id");
        String boardType = (String) board.get("type");
        String verticesKey = (String) board.get("verticesKey");
        String neighboursKey = (String) board.get("neighboursKey");
        int maxNeighbours = (int) (long) board.get("maxNeighbours");

        properties.put(Hash.GetInstance().hash("boardType"), new PropertyString("boardType", boardType));
        if (board.get("img") != null) {
            properties.put(imgHash, new PropertyString("img", (String) board.get("img")));
        }

        JSONArray nodeList = (JSONArray) board.get("nodes");

        for(Object o : nodeList)
        {
            // Add nodes to board nodes
            JSONObject node = (JSONObject) o;
            CCNode newBN = new CCNode();
            newBN.loadBoardNode(node);
            newBN.setComponentName(((PropertyString)newBN.getProperty(nameHash)).value);
            newBN.setMaxNeighbours(maxNeighbours);
            boardNodes.add(newBN);
        }

        int _hash_neighbours_ = Hash.GetInstance().hash(neighboursKey);
        int _hash_vertices_ = Hash.GetInstance().hash(verticesKey);

        for (CCNode bn : boardNodes) {
            Property p = bn.getProperty(_hash_neighbours_);
            if (p instanceof PropertyStringArray) {
                PropertyStringArray psa = (PropertyStringArray) p;
                for (String str : psa.getValues()) {
                    CCNode neigh = this.getNodeByProperty(_hash_vertices_, new PropertyString(str));
                    if (neigh != null) {
                        bn.addNeighbour(neigh);
                        neigh.addNeighbour(bn);
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof StarBoard) {
            StarBoard other = (StarBoard) o;
            return componentID == other.componentID && other.boardNodes.equals(boardNodes);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(componentID, boardNodes);
    }

    @Override
    public List<CCNode> getComponents() {
        return getBoardNodes();
    }

    @Override
    public CoreConstants.VisibilityMode getVisibilityMode() {
        return CoreConstants.VisibilityMode.VISIBLE_TO_ALL;
    }
}
