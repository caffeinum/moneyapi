HOST="http://localhost:8080"

echo "Testing user creation at $HOST/users"

echo "Get user who is not existant"
curl $HOST/users/1111 && echo

echo "Create user admin"
curl -X POST $HOST/users -d "username=admin" && echo
echo "Create user root"
curl -X POST $HOST/users -d "username=root" && echo
echo "Testing another user wth the same name"
curl -X POST $HOST/users -d "username=root" && echo

echo "Deposit user root twice by 100"
curl -X PUT $HOST/users/1/deposit -d "amount=100" && echo
curl -X PUT $HOST/users/1/deposit -d "amount=100" && echo

echo "Send money from root to admin amount 150"
curl -X PUT $HOST/users/1/send/0 -d "amount=150" && echo

echo "Send money from root to admin amount 100 but he does not have that much"
curl -X PUT $HOST/users/1/send/0 -d "amount=100" && echo

echo "He tries to withdraw 100 but fails too"
curl -X PUT $HOST/users/1/withdraw -d "amount=100" && echo

echo "He tries to withdraw 50 and succeeds"
curl -X PUT $HOST/users/1/withdraw -d "amount=50" && echo



U1=1
U2=2
echo "Deal with concurrency"
COUNT=5
echo "We try $COUNT times to send back and forth money and see if the system fails to maintain zero-sum"
for i in 1..$COUNT
do	
	amount=($RANDOM % 100)
	echo "$U1 -> $U2: send $amount"
	curl -X PUT $HOST/users/$U1/send/$U2 -d "amount=$amount" &
	
	amount=($RANDOM % 100)
	echo "$U2 -> $U1: send $amount"
	curl -X PUT $HOST/users/$U2/send/$U1 -d "amount=$amount" &
done

wait

echo "At the end, balances are:"
curl $HOST/users/0 && echo
curl $HOST/users/1 && echo






