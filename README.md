# DesafioCriptAA

# Conclusões
## 1. Pseudorandomness
O DPRG cria cada bloco do keystream com o seguinte calculo:
SHA256(key || counter)

Onde:
key – segredo partilhado entre servidor e proxy
counter – contador incremental de frames

O counter garante que cada bloco criado é único, e evita a repetição de keystream a cada bloco, mesmo que a chave seja reutilizada.

## 2. Reproducibility
Tanto o servidor quanto o proxys utilizam a mesma chave inicial
O servidor crifra então cada frame com a keystream criada pelo o DPRG, e o proxys usa o mesmo DPRG para descifrar depois o frame.

## 3. Synchronization and Fault Tolerance (a melhorar)
### AES - GCM && ChaCha20-Poly1305
No caso do AES-GCM e do ChaCha20-Poly1305, cada frame é cifrado de forma independete (com IV e chave). Isso garante que mesmo que se perca pacotes o UDP conseguirá cifrar os seguintes frames na mesma, uma vez que são independentes. A mesma explicação vale para a duplicação e para frames recebidos fora de ordem, o user talvez seja capaz de verificar alguns frames fora de ordem.

### (TODO) DPRG
A maior fraqueza por enquanto é esta.
Se o UDP perder um único frame, todos os frames a seguir a esse são descifrados incorretamente, uma vez que 'SHA256(key || counter)' terá o counter errado.

## 4. Eficiência
O Desafio 3, utiliza uma abordagem mais simples para gerar um Deterministic Pseudorandom Generator mais eficiente, e desta forma é computacionalmente mais eficiente.

# Metrics Desafio 1
O Video para se travar a meio o UDP

## Server
real    2m8,797s -> 128s
user    0m5,006s
sys     0m2,498s

Performance counter stats for 'java hjStreamServer movies/cars.dat localhost 8888':

        7869579966      task-clock:u                     #    0,061 CPUs utilized             
                 0      context-switches:u               #    0,000 /sec                      
                 0      cpu-migrations:u                 #    0,000 /sec                      
             53612      page-faults:u                    #    6,813 K/sec                     
        6971000750      instructions:u                   #    0,52  insn per cycle            
                                                  #    0,26  stalled cycles per insn   
       13343711121      cycles:u                         #    1,696 GHz                       
        1780833431      stalled-cycles-frontend:u        #   13,35% frontend cycles idle      
        1726005075      stalled-cycles-backend:u         #   12,93% backend cycles idle       
        1264744074      branches:u                       #  160,713 M/sec                     
          67037683      branch-misses:u                  #    5,30% of all branches           

     128,801613855 seconds time elapsed

       4,922827000 seconds user
       3,054525000 seconds sys

## UDP
real    2m14,175s
user    0m4,559s
sys     0m1,902s

 Performance counter stats for 'java hjUDPproxy':

        7119905194      task-clock                       #    0,049 CPUs utilized             
             37492      context-switches                 #    5,266 K/sec                     
              3652      cpu-migrations                   #  512,928 /sec                      
             59725      page-faults                      #    8,388 K/sec                     
        9117195524      instructions                     #    0,50  insn per cycle            
                                                  #    0,30  stalled cycles per insn   
       18247873220      cycles                           #    2,563 GHz                       
        2224706609      stalled-cycles-frontend          #   12,19% frontend cycles idle      
        2726866611      stalled-cycles-backend           #   14,94% backend cycles idle       
        1712109283      branches                         #  240,468 M/sec                     
          94030957      branch-misses                    #    5,49% of all branches           

     146,185349339 seconds time elapsed

       4,962608000 seconds user
       2,254187000 seconds sys


# Metrics Desafio 2
O Video para se travar a meio o UDP

## Server
real    2m13,139s
user    0m5,274s
sys     0m1,833s

 Performance counter stats for 'java hjStreamServer movies/cars.dat localhost 8888':

        8142137165      task-clock:u                     #    0,063 CPUs utilized             
                 0      context-switches:u               #    0,000 /sec                      
                 0      cpu-migrations:u                 #    0,000 /sec                      
             57355      page-faults:u                    #    7,044 K/sec                     
        8222489159      instructions:u                   #    0,59  insn per cycle            
                                                  #    0,25  stalled cycles per insn   
       13992995214      cycles:u                         #    1,719 GHz                       
        1918899665      stalled-cycles-frontend:u        #   13,71% frontend cycles idle      
        2019380369      stalled-cycles-backend:u         #   14,43% backend cycles idle       
        1519581927      branches:u                       #  186,632 M/sec                     
          71097608      branch-misses:u                  #    4,68% of all branches           

     128,786210673 seconds time elapsed

       5,188735000 seconds user
       3,083161000 seconds sys

## UDP
real    2m8,798s -> 128s
user    0m5,000s
sys     0m2,570s

 Performance counter stats for 'java hjUDPproxy':

        7051146769      task-clock:u                     #    0,053 CPUs utilized             
                 0      context-switches:u               #    0,000 /sec                      
                 0      cpu-migrations:u                 #    0,000 /sec                      
             60188      page-faults:u                    #    8,536 K/sec                     
        8060555934      instructions:u                   #    0,61  insn per cycle            
                                                  #    0,27  stalled cycles per insn   
       13302661570      cycles:u                         #    1,887 GHz                       
        1832305964      stalled-cycles-frontend:u        #   13,77% frontend cycles idle      
        2172323483      stalled-cycles-backend:u         #   16,33% backend cycles idle       
        1497046253      branches:u                       #  212,312 M/sec                     
          67272617      branch-misses:u                  #    4,49% of all branches           

     133,268070164 seconds time elapsed

       4,933497000 seconds user
       2,239921000 seconds sys


# Metrics Desafio 3
O Video para se travar a meio o UDP

## Server
real    2m8,779s -> 128s
user    0m2,927s
sys     0m2,475s

 Performance counter stats for 'java hjStreamServer movies/cars.dat localhost 8888':

        5941122013      task-clock:u                     #    0,046 CPUs utilized             
                 0      context-switches:u               #    0,000 /sec                      
                 0      cpu-migrations:u                 #    0,000 /sec                      
             49706      page-faults:u                    #    8,366 K/sec                     
        4469906821      instructions:u                   #    0,58  insn per cycle            
                                                  #    0,25  stalled cycles per insn   
        7672225866      cycles:u                         #    1,291 GHz                       
        1124850022      stalled-cycles-frontend:u        #   14,66% frontend cycles idle      
         922649742      stalled-cycles-backend:u         #   12,03% backend cycles idle       
         869933882      branches:u                       #  146,426 M/sec                     
          32885631      branch-misses:u                  #    3,78% of all branches           

     128,782626372 seconds time elapsed

       3,013725000 seconds user
       3,049461000 seconds sys

## UDP
real    2m14,631s
user    0m3,064s
sys     0m1,576s

 Performance counter stats for 'java hjUDPproxy':

        5028793829      task-clock:u                     #    0,038 CPUs utilized             
                 0      context-switches:u               #    0,000 /sec                      
                 0      cpu-migrations:u                 #    0,000 /sec                      
             48537      page-faults:u                    #    9,652 K/sec                     
        5162406852      instructions:u                   #    0,72  insn per cycle            
                                                  #    0,20  stalled cycles per insn   
        7215391495      cycles:u                         #    1,435 GHz                       
        1052639216      stalled-cycles-frontend:u        #   14,59% frontend cycles idle      
        1055994616      stalled-cycles-backend:u         #   14,64% backend cycles idle       
         988251471      branches:u                       #  196,519 M/sec                     
          30721753      branch-misses:u                  #    3,11% of all branches           

     130,961885032 seconds time elapsed

       2,891468000 seconds user
       2,262005000 seconds sys