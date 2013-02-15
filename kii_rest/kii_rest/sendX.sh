for i in `seq 1 200`
do
    python sendmessage.py
    echo $i times done
    echo time: `date +%s`
    sleep 5
done
