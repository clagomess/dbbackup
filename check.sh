if [ -z "$1" ] || [ -z "$1" ] || [ -z "$1" ]; then
  echo "Necessário informar parametros!"
  echo "./check.sh host port script"
  echo "Exemplo: ./check.sh 127.0.0.1 80 \"ls\""
  exit 0
fi

result=1

while [ $result = 1 ]; do
    echo "Tentando conexao $1:$2"
    nc -z $1 $2 > /dev/null 2>&1
    result=$?

    if [ $result = 0 ]; then
      echo "Sucesso"
      break
    fi

    sleep 4
done

exec "$3"