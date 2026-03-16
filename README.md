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