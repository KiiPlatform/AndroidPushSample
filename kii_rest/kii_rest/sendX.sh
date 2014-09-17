for i in `seq 1 $1`
do
    python sendmessage.py
    echo $i times done
    echo time: `date +%s`
    sleep 2
done
