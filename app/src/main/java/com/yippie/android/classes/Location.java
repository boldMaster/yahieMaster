package com.yippie.android.classes;

import java.util.ArrayList;
import java.util.List;

public class Location
{
    /**
     * Subclass of location. This class will store country information
     */
    public static class Country
    {
        Integer countryId;
        Integer countryName;
        List<State> stateList;
    }

    /**
     * Subclass of the location, This class will store state information
     */
    public static class State
    {
        Integer stateId;
        String stateName;
        List<StateArea> areaList;

        /**
         * Constructor
         * @param stateId - The id of the state
         * @param stateName - The name of the state
         * @param areaList - the list of area available in this state
         */
        public State(Integer stateId, String stateName, ArrayList<StateArea>areaList)
        {
            this.stateId = stateId;
            this.stateName = stateName;
            this.areaList = areaList;
        }

        // Getter
        public Integer getStateId(){
            return this.stateId;
        }
        public String getStateName(){
            return this.stateName;
        }
        public List<StateArea> getStateAreaList(){
            return this.areaList;
        }

        /**
         * Function to get state information
         * @param stateId - The id of the state
         */
        public static State getStateInfo(Integer stateId){
            // Get state list
            List<State> stateList = getMalaysiaStateList();
            State stateInfo = null;
            // Loop through the state list and find the correct state
            for(State curState : stateList){
                if(curState.getStateId().equals(stateId)){
                    stateInfo = curState;
                    break;
                }
            }//for(State curState : stateList){

            return stateInfo;
        }
    }

    /**
     * Subclass of the location class. This class will store the state area
     */
    public static class StateArea
    {
        Integer areaId;
        String areaName;

        /**
         * Constructor
         * @param areaId - The id of the area
         * @param areaName - The name of the area
         */
        public StateArea(Integer areaId, String areaName)
        {
            this.areaId = areaId;
            this.areaName = areaName;
        }

        // Getter
        public Integer getAreaId(){
            return this.areaId;
        }
        public String getAreaName(){
            return this.areaName;
        }
    }

    /**
     * Function to return the list of the State in malaysia
     */
    public static List<State> getMalaysiaStateList()
    {
        List<State> malaysiaStateList = new ArrayList<State>();

        // Add state into the list
        malaysiaStateList.add(new State(1,"Penang",null));
        malaysiaStateList.add(new State(2,"Kuala Lumpur",null));
        malaysiaStateList.add(new State(3,"Selangor",null));
        malaysiaStateList.add(new State(4,"Johor",null));
        malaysiaStateList.add(new State(5,"Perak",null));
        malaysiaStateList.add(new State(6,"Kedah",null));
        malaysiaStateList.add(new State(7,"Malacca",null));
        malaysiaStateList.add(new State(8,"Pahang",null));
        malaysiaStateList.add(new State(9,"Perlis",null));
        malaysiaStateList.add(new State(10,"Terengganu",null));
        malaysiaStateList.add(new State(11,"Kuantan",null));
        malaysiaStateList.add(new State(12,"Sabah",null));
        malaysiaStateList.add(new State(13,"Sarawak",null));
        malaysiaStateList.add(new State(14,"Negeri Sembilan",null));
        malaysiaStateList.add(new State(15,"Labuan",null));
        malaysiaStateList.add(new State(16,"Putrajaya",null));

        return malaysiaStateList;
    }
}
