# coimbra

## Data Treatment
Data Treatment organizes and generates the synthetic population based on the survey information
Follow the Order
1. Use: [coimbra_filtro2.tsv](data/population/coimbra_filtro2.tsv) (this tsv was already filtered in terms of errors of the survey )
1. Generate Synthetic Population:
    1. [SyntheticPoPulation_v1.java](src/main/java/pt/mvilaca/matsimtests/dataTreatment/SyntheticPopulation_v1.java) simplified version that just replicate the trips based on coef_exp  
    1. [SyntheticPopulation_v6.java](src/main/java/pt/mvilaca/matsimtests/dataTreatment/SyntheticPopulation_v6.java):     
       * Este Código replica o número de viagens descritas no inquérito de mobilidade as vezes descritas pelo coef_exp  
       * cria um banco de coordenadas por zona e selecciona de forma aleatória coordenadas para as viagens replicadas;
       * faz variar a hora de partida +/-10' e cria IDs unicos para as novas viagens
 1. [Filtro3](/src/main/java/pt/mvilaca/matsimtests/dataTreatment/Filtro3.java): Remove tudo o que é fora da SMM Coimbra
 1. [FiltroReducaoEscala_EWGT](src/main/java/pt/mvilaca/matsimtests/dataTreatment/FiltroReducaoEscala_EWGT.java): Reduz à escala do EWGT
 

 ## Population
 1. After generating the synthetic population in a tsv file the class [CoimbraQuestionario3.java](src/main/java/pt/mvilaca/matsimtests/population/CoimbraQuestionario3.java) is prepared to read and process the tsv file according to matsim structure. 
 1. To launch this class [Population_ewgt.java](src/main/java/pt/mvilaca/matsimtests/population/Population_EWGT.java) is used. Here the directory to the population and facilities xml should be defined.
 

 ## Network
 1. The convertion of the network is made through a osm file  [Coimbra_EWGTOsmToNetwork.java](src/main/java/pt/mvilaca/matsimtests/network/Coimbra_EWGTOsmToNetwork.java).
 
 
 ## PT aka 'transport'
 1. The conversion of ftfs files is made by the class [EWGT_Transport_MV.java](src/main/java/pt/mvilaca/matsimtests/transport/EWGT_Transport_MV.java), based on this the xml files schedule, vehicles and network are designed or redesigned.
 Note: the population is generated using the new network with PT at: [Population_ewgt.java](src/main/java/pt/mvilaca/matsimtests/population/Population_EWGT.java)
 
 ## Baseline Simulation
 1. Configuration and simulation of the baseline scenario [CoimbraRegionWPTandSyntheticPopulation_EWGT](src/main/java/pt/mvilaca/matsimtests/CoimbraRegionWPTandSyntheticPopulation_EWGT.java)
 
 ## DRT (case study applicability)
 1. Artificially replace the transport mode in the xml population file through the [PopulationDRT.java](src/main/java/DRT/PopulationDRT.java)
 1. [vehiclesXml.java](src/main/java/DRT/vehiclesXml.java): to create the drt vehicle fleet with 4 passengers capacity
 1. [TestDrtCreator.java](src/main/java/DRT/TestDrtCreator.java): To run the simulation including the drt. Opposite to the baseline scenario the configuration (config.xml) file is edited with an outside code editor and the new version is updated to run the new inputs. 
 Note: Revise all the config file to be sure that the parameters are acording to the simulation goals (comments about each parameter are provided in the config.xml)
  
  
 

