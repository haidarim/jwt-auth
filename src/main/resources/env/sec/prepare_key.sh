# !/bin/bash


BASE=$(grep 'jwt-key.base:' ../../application.yml | awk '{print $2}')
ALGO=$(grep 'jwt-key.algo:' ../../application.yml | awk '{print $2}')
LENGTH=$(grep 'jwt-key.length:' ../../application.yml | awk '{print $2}')
HMAC_KEY=""
RS_PR_KEY="" # for generation i.e. signing
RS_PUB_KEY="" # for verify

echo "defined base: $BASE"
echo "defined algo: $ALGO"
echo "defined length: $LENGTH"

# default values :-
BASE=${BASE:-base64}
ALGO=${ALGO:-HS256}
LENGTH=${LENGTH:-32}

if [[ "$ALGO" == HS* ]]; then # generate using HMAC algo
  HMAC_KEY=$(openssl rand -"$BASE" $LENGTH)
  echo "HMAC key generated!"
elif [[ "$ALGO" == RS* ]];then # generate using RSA algo
  RS_PR_KEY=$(openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 2>/dev/null)
  RS_PUB_KEY=$(echo "$RS_PR_KEY" | openssl rsa -pubout 2>/dev/null)

  echo "RSA keypair generated:"
else
  echo "unsupported algorithm: $ALGO"
  exit 1
fi


echo "the key is: $HMAC_KEY"

if [[ "$ALGO" == HS* ]]]; then
  vault kv put secret/jwt hs_secret=$HMAC_KEY
elif [[ "$ALGO" == RS* ]]; then
  vault kv put secret/jwt rs_pr_secret=$RS_PR_KEY
  vault kv put secret/jwt rs_pub_secret=$RS_PUB_KEY
fi


