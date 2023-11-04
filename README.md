# coimbra

## Data treatment 

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
 1. [FiltroReduçãoEscala_EWGT](src/main/java/pt/mvilaca/matsimtests/dataTreatment/FiltroReducaoEscala_EWGT.java): Reduz à escala do EWGT

