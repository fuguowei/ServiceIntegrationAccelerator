/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.introspection;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.BitSet;
import qut.edu.au.services.Group;
import qut.edu.au.services.Parameter;

/**
 *
 * @author fuguo
 */
public class ServiceSimulator {
    /*
     ArrayList<String> parameters = [Key,"Password,"AccountNumber,"MeterNumber,"IntegratorId,"LanguageCode,
     "LocaleCode,"CustomerTransactionId,"LanguageCode,"LocaleCode,"Options,"Index,"Type,"
     Index,"Date,"Actions,"ShipTimestamp,"DropoffType,"ServiceType,"PackagingType,"
     ManifestReferenceType,"Units,"Value,"Currency,"Amount,"Length,"Width,"Height,"
     Units, PreferredCurrency, AccountNumber, TinType, Number, Usage, EffectiveDate, 
     ExpirationDate, ContactId, PersonName, Title, CompanyName, PhoneNumber, 
     PhoneExtension, TollFreePhoneNumber, PagerNumber, FaxNumber, EMailAddress, 
     StreetLines, City, StateOrProvinceCode, PostalCode, UrbanizationCode, CountryCode, 
     CountryName, Residential, AccountNumber, TinType, Number, Usage, EffectiveDate, 
     ExpirationDate, ContactId, PersonName, Title, CompanyName, PhoneNumber, PhoneExtension, 
     TollFreePhoneNumber, PagerNumber, FaxNumber, EMailAddress, StreetLines, City, StateOrProvinceCode, 
     PostalCode, UrbanizationCode, CountryCode, CountryName, Residential, RecipientLocationNumber, 
     ContactId, PersonName, Title, CompanyName, PhoneNumber, PhoneExtension, TollFreePhoneNumber, 
     PagerNumber, FaxNumber, EMailAddress, StreetLines, City, StateOrProvinceCode, PostalCode, 
     UrbanizationCode, CountryCode, CountryName, Residential, AccountNumber, TinType, Number, 
     Usage, EffectiveDate, ExpirationDate, ContactId, PersonName, Title, CompanyName, 
     PhoneNumber, PhoneExtension, TollFreePhoneNumber, PagerNumber, FaxNumber, EMailAddress, 
     StreetLines, City, StateOrProvinceCode, PostalCode, UrbanizationCode, CountryCode, CountryName, 
     Residential, PaymentType, AccountNumber, TinType, Number, Usage, EffectiveDate, ExpirationDate, 
     ContactId, PersonName, Title, CompanyName, PhoneNumber, PhoneExtension, TollFreePhoneNumber, 
     PagerNumber, FaxNumber, EMailAddress, StreetLines, City, StateOrProvinceCode, PostalCode, 
     UrbanizationCode, CountryCode, CountryName, Residential, Type, AccountNumber, 
     SpecialServiceTypes, Currency, Amount, RateTypeBasis, ChargeBasis, 
     ChargeBasisLevel, CollectionType, AccountNumber, TinType, Number, Usage, EffectiveDate, 
     ExpirationDate, ContactId, PersonName, Title, CompanyName, PhoneNumber, PhoneExtension, 
     TollFreePhoneNumber, PagerNumber, FaxNumber, EMailAddress, StreetLines, City, StateOrProvinceCode, 
     PostalCode, UrbanizationCode, CountryCode, CountryName, Residential, ContactId, PersonName];
     */

    //private ArrayList<String> parameters = new ArrayList<String>[]{"Key", "Password"};
    //private static final List<String> parameters = Arrays.asList("Key", "Password");
    private ArrayList<String> parameters = null;
    private BitSet bitSetParameters = null;
    //private static final int minimumNumberOfValidCombinations = 5;
    private ArrayList<Integer> indices = null;
    private ArrayList<BitSet> validParameterSets = null;
    private final Random rng = new Random(System.currentTimeMillis());

    /* --- for track */
    /*
     public static int successfulPathIndices[][] = {{1, 2, 3, 4, 13, 20, 32}, {1, 2, 3, 4, 15, 20, 32},
     {1, 2, 3, 4, 15, 21, 32}, {1, 2, 3, 4, 16, 21, 31}, {1, 2, 3, 4, 15, 21, 28}, {1, 2, 3, 4, 12, 15, 21, 30}, {1, 2, 3, 4, 14, 18, 29}, {1, 2, 3, 4, 12, 18, 29}, {1, 2, 3, 4, 11, 18, 31}, {1, 2, 3, 4, 12, 18, 31},
     {1, 2, 3, 4, 6, 7, 8, 11, 13, 14, 18, 19, 20, 21, 22, 23, 25}};
     */
    //--- for Ship service - processShipmentOperation
    public static int realService[][] = {
    {1,2,3,4,7,8,10,11,12,15,16,17,18,19,20,22,23,24,25,26},
    {1,2,3,4,6,7,8,9,10,11,12,15,16,17,18,19,20,28},
    {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21},
    {1,2,3,4,7,8,10,11,12,15,16,17,18,19,20,22,23,24,25,26,27,28},
    {1,2,3,4,7,8,10,11,12,15,16,17,18,19,20,22,23,24,25,28},
    {1,2,3,4,7,8,10,11,12,13,15,16,17,18,19,20,22,23,24,25,27,28,29,30},
    {1,2,3,4,7,8,10,11,12,13,15,16,17,18,19,20,23,24,25,26,28,30,31},
    {1,2,3,4,7,8,10,11,12,15,16,17,18,19,20,23,24,25,26,28,30,31},
    {1,2,3,4,6,7,8,10,11,12,15,16,17,18,19,20,32,33,34,35,36,37,38,39,40,41},
    {1,2,3,4,7,8,10,11,12,15,16,17,18,19,20,28,42},
    {1,2,3,4,6,7,8,9,10,11,12,13,15,16,17,18,19,27,28,42,43},
    };

    //private int successfulPathIndices[][][];
    private ArrayList<ArrayList<ArrayList<Parameter>>> successfulPathIndices = new ArrayList<ArrayList<ArrayList<Parameter>>>();
    //private int currentSuccessfulPathIndices[][];
    private ArrayList<ArrayList<Integer>> currentSuccessfulPathIndices;
    //private static int heights[] = {100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200};
    //private static final int heights[] = {10};
    private ArrayList<Group> groups = new ArrayList<Group>();

    public ArrayList<Group> getGroups() {
        return groups;
    }
    
    
    private ArrayList<Parameter> params = new ArrayList<Parameter>();
    //public static final int totalNumberofPaths = realService.length;
    //private ArrayList<ArrayList<Integer>> successfulPathIndices = new ArrayList<ArrayList<Integer>>();

    
    public ArrayList<Parameter> getParams() {
        return params;
    }
    
    public void addParam(Parameter param) {
        params.add(param);
    }
    
    public void setCurrentSuccessfulPathIndices(ArrayList<ArrayList<Integer>> successfulPaths) {
        if (successfulPaths == null) {
            ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();
            for (int[] realService1 : realService) {
                ArrayList<Integer> integers = new ArrayList<Integer>();
                for (int j = 0; j < realService1.length; j++) {
                    integers.add(realService1[j]);
                }
                paths.add(integers);
                //currentSuccessfulPathIndices.add(indices)
            }
            this.currentSuccessfulPathIndices = paths;
        } else {
            this.currentSuccessfulPathIndices = successfulPaths;
        }
        generateValidCombinationsBitSetVersion();
    }

    public ArrayList<ArrayList<Integer>> getCurrentSuccessfulPathIndices() {
        return currentSuccessfulPathIndices;
    }

    public ArrayList<ArrayList<ArrayList<Parameter>>> getSuccessfulPathIndices() {
        return successfulPathIndices;
    }

    public void generateSuccessfulPaths(boolean normal) {
        // a set of height

        //successfulPathIndices[i][j][k]
        if (!successfulPathIndices.isEmpty()) {
            successfulPathIndices.clear();
        }        
        if (MonteCarloIntrospection.realService.equals("TRUE")) {
            ArrayList<ArrayList<Parameter>> paths = new ArrayList<ArrayList<Parameter>>();
            for (int[] realService1 : realService) {
                ArrayList<Parameter> individualPath = new ArrayList<Parameter>();                
                for (int j = 0; j < realService1.length; j++) {
                    for (Parameter param : params) {
                        if (param.getSimpleIndex()== realService1[j]) {
                            individualPath.add(param);
                            break;
                        }
                    }
                }
                paths.add(individualPath);
                //currentSuccessfulPathIndices.add(indices)
            }
            successfulPathIndices.add(paths);
        } else {
            for (Integer height1 : MonteCarloIntrospection.heights) {
                //uniform first
                ArrayList<ArrayList<Parameter>> paths = generateExperimentLists(height1, 
                        MonteCarloIntrospection.totalNumberofPaths - 1, normal, MonteCarloIntrospection.maxNumberOfParameters);
                successfulPathIndices.add(paths);
                /*
                for (int j = 0; j < paths.size(); j++) {
                ArrayList<Integer> path = paths.get(j);
                if (successfulPathIndices == null) {
                successfulPathIndices = new int[heights.length][paths.size()][path.size()];
                }
                for (int k = 0; k < path.size(); k++)
                successfulPathIndices[i][j][k] = path.get(k);
                }
                */
            }
        }
        
        //this.successfulPathIndices = successfulPathIndices;
    }

    public ArrayList<ArrayList<Parameter>> generateExperimentLists(int height, int numberOfList, boolean normal, int maxIndex) {
        ArrayList<ArrayList<Parameter>> result = new ArrayList<ArrayList<Parameter>>();
        //generate a known path based on the height, taking group boundary into consideration? and known groups?
        ArrayList<Parameter> firstKnownPath = generateFirstKnownPath(height, maxIndex, 3);
        result.add(firstKnownPath);
        ArrayList<Group> knownGroups = new ArrayList<Group>();
        for (Parameter parameter : firstKnownPath) {
            Group group = parameter.getGroupBelongTo();
            if (!knownGroups.contains(group)) {
                knownGroups.add(group);
            }
        }

        int counter = 0;
        while (counter < numberOfList) {
            result.add(generateAcceptedPath(firstKnownPath, knownGroups, maxIndex, normal));
            counter = counter + 1;
        }
        return result;
    }

    public ArrayList<BitSet> getValidParameterSets() {
        return validParameterSets;
    }

    public void initialiseParameters() {
        for (int i = 0; i < MonteCarloIntrospection.maxNumberOfParameters; i++) {
            parameters.add("parameter" + i);
        }
        for (int i = 0; i < MonteCarloIntrospection.maxNumberOfParameters; i++) {
            indices.add(i);
        }
    }
    /*
     public void generateValidCombinations() {
     ArrayList<String> combinationTemp = new ArrayList<String>();
     combinationTemp.add("parameter1");
     combinationTemp.add("parameter2");
     combinationTemp.add("parameter3");
     combinationTemp.add("parameter4");
     combinationTemp.add("parameter5");
     validCombinations.add(combinationTemp);

     for (int i = 0; i < totalNumberOfValidParameters; i++) {
     Random randomGenerator = new Random();

     int actualNumberOfParameters = randomGenerator.nextInt(maxNumberOfParameters - minimumNumberOfValidCombinations + 1) + minimumNumberOfValidCombinations;
     ArrayList<String> combination = new ArrayList<String>();
     for (int j = 0; j < actualNumberOfParameters; j++) {
     int index = randomGenerator.nextInt(maxNumberOfParameters);
     combination.add(parameters.get(index));
     }
     validCombinations.add(combination);
     }
     }
     */

    private void generateValidCombinationsBitSetVersion() {
        //BitSet tempSet = new BitSet();
        //tempSet.set(2, 5, true);
        //validParameterSets.add(tempSet);
        // shuffle the indices
        // take the first 20% and then inverse the value of the bit set       
        //Collections.shuffle(Arrays.asList(bitSetParameters));
        /* this is the old random generation code
        int counter =0;
        while (counter < totalNumberOfValidParameters)
        {
        Random randomGenerator = new Random();
        double distribution = randomGenerator.nextDouble();
        //double distribution = randomGenerator.nextGaussian();
        if (distribution <= differenceFactor) {
        Collections.shuffle(indices);
        int numberOfBitsToInverse = (int) (differenceFactor * maxNumberOfParameters);
        //only inverse 20% of them, the first 20%?
        for (int j = 0; j < numberOfBitsToInverse; j++) {
        int index = indices.get(j);
        bitSetParameters.flip(index);
        }
        validParameterSets.add(bitSetParameters);
        counter ++;
        }
        }
         */
        //special case, intentionally set this  //base case
        validParameterSets = new ArrayList<BitSet>();
        for (ArrayList<Integer> currentSuccessfulPathIndice : this.currentSuccessfulPathIndices) {
            BitSet tempBitSet = new BitSet(MonteCarloIntrospection.maxNumberOfParameters);
            for (Integer currentSuccessfulPathIndice1 : currentSuccessfulPathIndice) {
                tempBitSet.set(currentSuccessfulPathIndice1);
            }
            validParameterSets.add(tempBitSet);
        }

    }

    public ServiceSimulator() {
        indices = new ArrayList<Integer>();
        parameters = new ArrayList<String>();
        int maxNumberOfParameters= MonteCarloIntrospection.maxNumberOfParameters;
        
        bitSetParameters = new BitSet(maxNumberOfParameters);
        validParameterSets = new ArrayList<BitSet>();

        //initialise the groups and parameters        
        int groupSize = rng.nextInt(maxNumberOfParameters / MonteCarloIntrospection.groupGap) + 1;
        int counter = 0;
        int numberOfGroup = 1;
        Group group = new Group(numberOfGroup);
        groups.add(group);

        for (int i = 1; i <= maxNumberOfParameters; i++) {
            Parameter param = new Parameter();
            param.setSimpleIndex(i);
            param.setName("Parameter" + i);
            if (i <= MonteCarloIntrospection.numberOfCompulsoryParameters) {
                param.setCompulsory(true);
            }

            params.add(param);
            if (counter < groupSize) {
                counter++;
            } else {
                numberOfGroup++;
                //create a new group
                groupSize = rng.nextInt(maxNumberOfParameters / MonteCarloIntrospection.groupGap) + 1;
                group = new Group(numberOfGroup);
                groups.add(group);
                counter = 0;
            }
            group.addParameter(param);
            param.setGroupBelongTo(group);
        }
    }

    public boolean equalLists(List<String> one, List<String> two) {
        if (one == null && two == null) {
            return true;
        }
        if ((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()) {
            return false;
        }

        //to avoid messing the order of the lists we will use a copy
        //as noted in comments by A. R. S.
        one = new ArrayList<String>(one);
        two = new ArrayList<String>(two);

        Collections.sort(one);
        Collections.sort(two);
        return one.equals(two);
    }
    /*
     public boolean serviceA(ArrayList<String> parametersSupplied) {
     initialiseParameters();
     generateValidCombinations();
     for (ArrayList<String> combination : validCombinations) {
     if (equalLists(combination, parametersSupplied)) {
     return true;
     }
     }
     return false;
     }
     */
    /*
    public boolean serviceA(BitSet parametersSupplied) {
        initialiseParameters();
        generateValidCombinationsBitSetVersion();
        for (BitSet set : validParameterSets) {
            if (set.equals(parametersSupplied)) {
                return true;
            }
        }
        return false;
    }
    */

    public boolean invokeOperation(ArrayList<Parameter> paramPath) {
        //initialiseParameters();
        BitSet parametersSupplied = new BitSet(MonteCarloIntrospection.maxNumberOfParameters);

        for (Parameter parameter : paramPath) {
            parametersSupplied.set(parameter.getSimpleIndex());
        }
        for (BitSet set : validParameterSets) {
            if (set.equals(parametersSupplied)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean invokeOperationV2(ArrayList<Integer> paramPath) {
        //initialiseParameters();
        BitSet parametersSupplied = new BitSet(MonteCarloIntrospection.maxNumberOfParameters);

        for (Integer parameter : paramPath) {
            parametersSupplied.set(parameter);
        }
        for (BitSet set : validParameterSets) {
            if (set.equals(parametersSupplied)) {
                return true;
            }
        }
        return false;
    }
    

    public BitSet getBitSetParameters() {
        return bitSetParameters;
    }

    public static void main(String[] args) {
        /*
        
         BitSet tempSet = new BitSet();
         tempSet.set(2, 5, true);

         ServiceSimulator provider = new ServiceSimulator();

         if (provider.serviceA(tempSet)) {
         System.out.println("the parameters are accepted!");
         } else {
         System.out.println("the parameters are rejected!");
         }

         for (BitSet set : provider.getValidParameterSets()) {
         for (int k = 0; k < maxNumberOfParameters; k++) {
         if (set.get(k)) {
         System.out.print("1");
         } else {
         System.out.print("0");
         }
         }
         System.out.println();
         }
         */
        /*
         ArrayList<Integer> parameters = new ArrayList<Integer>();
         for (int i = 0; i < 20; i++) {
         parameters.add(i);
         }

         Collections.shuffle(parameters, null);
         for (int i = 0; i < 20; i++) {
         System.out.print(parameters.get(i)+",");
         }
        
         */
        //for (int i = 0; i < 30; i++) {
        ServiceSimulator provider = new ServiceSimulator();

        ArrayList<ArrayList<Parameter>> paths = provider.
                generateExperimentLists(MonteCarloIntrospection.heights.get(0), 
                        MonteCarloIntrospection.totalNumberofPaths - 1, true, MonteCarloIntrospection.maxNumberOfParameters);
        //ArrayList<ArrayList<Integer>> paths = provider.generateExperimentLists(60, 20, null, 1200);
        System.out.println("total number of list: " + paths.size());
        for (ArrayList<Parameter> path : paths) {
            System.out.println("the size: " + path.size());
            int previousNumber = 0;
            for (Parameter param : path) {
                System.out.print(param.getSimpleIndex() + ",");
                if (previousNumber != 0 && previousNumber >= param.getSimpleIndex()) {
                    System.out.println("WRONG--------------");
                }
            }
            System.out.println();
            System.out.println();
        }

        //}
        //provider.generateValidCombinationsBitSetVersion();        
        //System.out.println(provider.getBitSetParameters());
        System.exit(1);
    }

    /*
     private ArrayList<Integer> generateFirstKnownGroupAndPath(int firstAfew, int height, int maxIndex, int step) {
     //generate the groups, groups should be less than the nubmer of parameters, i.e., the height/5, for example
     ArrayList<Integer> knownGroups = generateFirstKnownPath(firstAfew, height/5, maxIndex, step);
     //generate the known parameters
     ArrayList<Integer> knownPath = generateFirstKnownPath(firstAfew, height, maxIndex, step);
     //attach the parameters to the groups
        
     for (int i = 0; i < knownPath.size(); i++) {
            
     }
        
     }
     */
    /**
     *
     * @param firstAfew - the number of consecutive parameters at the beginning
     * @param height - the number of parameters in the path
     * @param maxIndex - the maximum index of the parameters in the path
     * @param step - the maximum number of consecutive parameters
     * @return
     */
    private ArrayList<Parameter> generateFirstKnownPath(int height, int maxIndex, int step) {
        //when we generate parameters, we sould generate their groups as well.

        ArrayList<Parameter> generated = new ArrayList<Parameter>();
        for (int i = 0; i < MonteCarloIntrospection.numberOfCompulsoryParameters; i++) {
            generated.add(params.get(i));
        }
        int next = MonteCarloIntrospection.numberOfCompulsoryParameters;
        int counter = 0;
        int upper = 0;
        //int inteval = maxNumber/height;
        Integer deviation = rng.nextInt(step);
        while (generated.size() < height && generated.get(generated.size() - 1).getSimpleIndex() < maxIndex) {
            int number = 0;
            if (counter < deviation) {
                number = next + 1;
                counter = counter + 1;
            } else {
                upper = next + maxIndex / (height - generated.size());
                if (upper > maxIndex) {
                    upper = maxIndex;
                }
                counter = 0;
                deviation = rng.nextInt(step);
                next = next + 1;
                number = rng.nextInt((upper - next) + 1) + next;
            }
            generated.add(params.get(number - 1));
            next = number;
        }
        /*
         System.out.println("the size: "+ generated.size());
         int previousNumber =0;
         for(Integer number: generated) {
         System.out.print(number+",");
         if (previousNumber != 0 && previousNumber >= number)
         System.out.println("WRONG--------------");
         previousNumber = number;
         }
         System.out.println();
         System.out.println();
         */
        return generated;
    }

    private boolean ifInGroup(Group baseGroup, int number) {
        boolean inBaseGroup = false;
        for (Parameter param : baseGroup.getParameters()) {
            if (param.getSimpleIndex() == number) {
                inBaseGroup = true;
                break;
            }
        }
        return inBaseGroup;
    }

    /**
     *
     * @param firstKnownPath - the first known path
     * @param firstAfew - the firstAfew of parameters will remain the same to
     * the ones in the first known path
     * @param maxIndex - the maximum index of the parameters in the path
     * @param step - the maximum number of consecutive parameters
     * @param gap - the gap of the movement
     * @normal - whether it is normal form distribution
     * @deviation - the deviation for normal distribution
     * @return
     */
    private ArrayList<Parameter> generateAcceptedPath(ArrayList<Parameter> firstKnownPath, ArrayList<Group> knownGroups, int maxIndex, boolean normal) {
        ArrayList<Integer> existingParamIndices = new ArrayList<Integer>();

        ArrayList<Parameter> generated = new ArrayList<Parameter>();
        for (int i = 0; i < MonteCarloIntrospection.numberOfCompulsoryParameters; i++) {
            generated.add(firstKnownPath.get(i));
            existingParamIndices.add(firstKnownPath.get(i).getSimpleIndex());
        }

        //int next = generated.get(generated.size() - 1);
        //int counter = 0;
        int upper = 0;
        //Integer variance = rng.nextInt(step);
        int index = MonteCarloIntrospection.numberOfCompulsoryParameters;
        //while (generated.get(generated.size() - 1).getSimpleIndex() < maxIndex && generated.size()<firstKnownPath.size()) {
        while (generated.get(generated.size() - 1).getSimpleIndex() < maxIndex) {
            Integer number = 0;
            int baseNumber = 0;
            Group baseGroup = null;
            if (index < firstKnownPath.size()) {
                baseNumber = firstKnownPath.get(index).getSimpleIndex();
                baseGroup = firstKnownPath.get(index).getGroupBelongTo();
            } else {
                baseNumber = generated.get(generated.size() - 1).getSimpleIndex();
                baseGroup = generated.get(generated.size() - 1).getGroupBelongTo();
            }

            //if (counter < variance) {
            //    number = next + 1;
            //    counter = counter + 1;
            //    index = index+1;
            //} else {
            //upper = baseNumber + gap;
            //counter = 0;
            //variance = rng.nextInt(step);
            //next = next + 1;
            int counter = 0;

            if (normal) {
                //number = (int)Math.round((double)s+rng.nextGaussian()*deviation);                    
                //number = (int)Math.round(rng.nextGaussian() * deviation);
                number = (int) Math.round(baseNumber + abs(rng.nextGaussian()) * MonteCarloIntrospection.experimentsValueDeviation);
                while (!ifInGroup(baseGroup, number) && counter < 3) { //if not in the base group, we try three times and then give up?
                    number = (int) Math.round(baseNumber + abs(rng.nextGaussian()) * MonteCarloIntrospection.experimentsValueDeviation);
                    counter++;
                }
                while (existingParamIndices.contains(number) || number < generated.get(generated.size() - 1).getSimpleIndex()) {
                    number = (int) Math.round(baseNumber + abs(rng.nextGaussian()) * MonteCarloIntrospection.experimentsValueDeviation);
                }
                if (number > maxIndex) {
                    break;
                }
            } else {
                upper = baseGroup.getParameters().get(baseGroup.getParameters().size() - 1).getSimpleIndex();

                if (upper > maxIndex) {
                    upper = maxIndex;
                }
                number = rng.nextInt((upper - baseNumber) + 1) + baseNumber;

                while (!ifInGroup(baseGroup, number) && counter < 3) { //if not in the base group, we try three times and then give up?
                    number = rng.nextInt((upper - baseNumber) + 1) + baseNumber;
                    counter++;
                }
                if (number > maxIndex) {
                    break;
                }

                while (existingParamIndices.contains(number) || number < generated.get(generated.size() - 1).getSimpleIndex()) {
                    number = rng.nextInt((upper - baseNumber) + 1) + baseNumber;
                    if (number > maxIndex) {
                        break;
                    }
                }
            }
            index = index + 1;
            //}
            generated.add(params.get(number - 1));
            existingParamIndices.add(params.get(number - 1).getSimpleIndex());
            //next = number;
        }

        /*
         System.out.println("the size: " + generated.size());
         int previousNumber = 0;
         for (Integer number : generated) {
         System.out.print(number + ",");
         if (previousNumber != 0 && previousNumber >= number) {
         System.out.println("WRONG--------------");
         }
         previousNumber = number;
         }
         System.out.println();
         System.out.println();
         */
        return generated;
    }
}
