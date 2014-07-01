'''
Created on 2013/01/28

@author: satoshi
'''

import ConfigParser
import logging
import httplib
import json
import time

CONFIG_FILE = 'setting.ini'

def getLogger():
    logger = logging.getLogger('debug')
    ch = logging.StreamHandler();
    ch.setLevel(logging.DEBUG)
    logger.addHandler(ch)
    logger.setLevel(logging.DEBUG)
    return logger

class ApiHelper(object):

    def __init__(self):
        conf = ConfigParser.SafeConfigParser()
        conf.read(CONFIG_FILE)
        self.appId = conf.get('app', 'app-id')
        self.appKey = conf.get('app', 'app-key')
        self.host = conf.get('app', 'host')
        self.gcmAppKey = conf.get('app', 'gcm-app-key')
        self.clientId = conf.get('app', 'client-id')
        self.jPushAppKey = conf.get('app', 'jpush-app-key')
        self.jPushMasterSecret = conf.get('app', 'jpush-master-secret')
        self.setCollapseKey=conf.get('app', 'set-collapse-key')
        self.clientSecret = conf.get('app', 'client-secret')
        self.appTopic = conf.get('constants', 'app-topic-name')
        self.message = conf.get('constants', 'push-message')
        self.appBucket= conf.get('constants', 'app-bucket-name')
        self.logger = getLogger()
        self.logger.debug('app id: ' + self.appId)
        self.logger.debug('app key: ' + self.appKey)
        self.logger.debug('base uri: ' + self.host)
        self.logger.debug('client id: ' + self.clientId)
        self.logger.debug('client secret: ' + self.clientSecret)
        self.logger.debug('gcm app key: ' + self.gcmAppKey)
        self.logger.debug('app topic name: ' + self.appTopic)
        self.logger.debug('push message: ' + self.message)
        self.getAppAdminToken()

    def setGCMKey(self):
        self.logger.debug('set gcm key')
        conn = httplib.HTTPConnection(self.host)
        path = '/api/apps/{0}/configuration/gcm'.format(self.appId);
        self.logger.debug('path: ' + path)
        body = {'gcmKey': self.gcmAppKey}
        headers = {'x-kii-appid': self.appId, 'x-kii-appkey': self.appKey,
                   'authorization': 'Bearer ' + self.token,
                   'content-type':
                   'application/vnd.kii.GCMKeyRegistrationRequest+json'}
        jsonBody = json.dumps(body);
        conn.request('PUT', path, jsonBody, headers)
        response = conn.getresponse()
        self.logger.debug('status: %d', response.status)
        self.logger.debug('body: %s', response.read())

    def getGCMKey(self):
        self.logger.debug('get gcm key')
        conn = httplib.HTTPSConnection(self.host)
        path = '/api/apps/{0}/configuration/gcm'.format(self.appId);
        headers = {'x-kii-appid': self.appId, 'x-kii-appkey': self.appKey, \
            'authorization': 'Bearer ' + self.token}
        self.logger.debug('path: ' + path)
        conn.request('GET', path, None, headers)
        response = conn.getresponse()
        jResp = json.load(response);
        gcmkey = jResp['gcmKey']
        self.logger.debug('status: %d', response.status)
        self.logger.debug('body: %s', jResp)
        self.logger.debug('gcmKey: ' + gcmkey)
        return jResp['gcmKey']

    def removeGCMKey(self):
        self.logger.debug('remove gcm key')
        conn = httplib.HTTPSConnection(self.host)
        path = '/api/apps/{0}/configuration/gcm'.format(self.appId);
        headers = {'x-kii-appid': self.appId, 'x-kii-appkey': self.appKey,
                   'authorization': 'Bearer ' + self.token}
        conn.request('DELETE', path, None, headers)
        response = conn.getresponse()
        self.logger.debug('status: %d', response.status)

    def setJPushKey(self):
        self.logger.debug('set jpush key')
        conn = httplib.HTTPConnection(self.host)
        path = '/api/apps/{0}/configuration/jpush'.format(self.appId);
        self.logger.debug('path: ' + path)
        body = {'appKey': self.jPushAppKey, 'masterKey': self.jPushMasterSecret}
        headers = {'x-kii-appid': self.appId, 'x-kii-appkey': self.appKey,
                'authorization': 'Bearer ' + self.token,
                'content-type':
                'application/vnd.kii.JPushKeyRegistrationRequest+json'}
        jsonBody = json.dumps(body);
        conn.request('PUT', path, jsonBody, headers)
        response = conn.getresponse()
        self.logger.debug('status: %d', response.status)
        self.logger.debug('body: %s', response.read())

    def getJPushKey(self):
        self.logger.debug('get jpush key')
        conn = httplib.HTTPSConnection(self.host)
        path = '/api/apps/{0}/configuration/jpush'.format(self.appId);
        headers = {'x-kii-appid': self.appId, 'x-kii-appkey': self.appKey, \
                'authorization': 'Bearer ' + self.token}
        self.logger.debug('path: ' + path)
        conn.request('GET', path, None, headers)
        response = conn.getresponse()
        jResp = json.load(response);
        jPushAppKey = jResp['appKey']
        jPushMasterSecret = jResp['masterKey']
        self.logger.debug('status: %d', response.status)
        self.logger.debug('body: %s', jResp)
        self.logger.debug('jPushAppKey: ' + jPushAppKey)
        self.logger.debug('jPushMasterSecret: ' + jPushMasterSecret)
        return jPushAppKey

    def removeJPushKey(self):
        self.logger.debug('remove jpush key')
        conn = httplib.HTTPSConnection(self.host)
        path = '/api/apps/{0}/configuration/jpush'.format(self.appId);
        headers = {'x-kii-appid': self.appId, 'x-kii-appkey': self.appKey,
                'authorization': 'Bearer ' + self.token}
        conn.request('DELETE', path, None, headers)
        response = conn.getresponse()
        self.logger.debug('status: %d', response.status)

    def getAppAdminToken(self):
        self.logger.debug('get token')
        conn = httplib.HTTPConnection(self.host)
        path = '/api/oauth2/token'
        body = {'client_id': self.clientId, 'client_secret': self.clientSecret}
        jsonBody = json.dumps(body)
        headers = {'x-kii-appid': self.appId, 'x-kii-appkey': self.appKey,
                'content-type': 'application/json'}
        conn.request('POST', path, jsonBody, headers)
        response = conn.getresponse()
        respDict = json.load(response)
        self.logger.debug('status: %d', response.status)
        self.logger.debug('body: %s', respDict)
        token = respDict['access_token']
        self.logger.debug('access-token: ' + token)
        self.token = token

    def createAppTopic(self):
        self.logger.debug('create app topic')
        conn = httplib.HTTPConnection(self.host)
        path = '/api/apps/{0}/topics/{1}'.format(self.appId, self.appTopic)
        headers = {'x-kii-appid': self.appId, 'x-kii-appkey': self.appKey}
        headers['authorization'] = 'Bearer ' + self.token
        headers['content-length'] = 0
        self.logger.debug('path: %s', path)
        conn.request('PUT', path, None, headers)
        response = conn.getresponse()
        self.logger.debug('status: %d', response.status)

    def grantSubscriptionOfAppTopic(self):
        self.logger.debug('create app topic')
        conn = httplib.HTTPConnection(self.host)
        path = '/api/apps/{0}/topics/{1}/acl/SUBSCRIBE_TO_TOPIC/UserID:ANY_AUTHENTICATED_USER'\
            .format(self.appId, self.appTopic)
        headers = {'x-kii-appid': self.appId, 'x-kii-appkey': self.appKey}
        headers['authorization'] = 'Bearer ' + self.token
        headers['content-length'] = 0
        self.logger.debug("path: %s", path)
        conn.request('PUT', path, None, headers)
        response = conn.getresponse()
        self.logger.debug('status: %d', response.status)

    def sendMessageToAppTopic(self):
        self.logger.debug('send message to app topic')
        conn = httplib.HTTPConnection(self.host)
        path = '/api/apps/{0}/topics/{1}/push/messages'\
            .format(self.appId, self.appTopic)
        headers = {'x-kii-appid': self.appId, 'x-kii-appkey': self.appKey}
        headers['authorization'] = 'Bearer ' + self.token
        headers['content-type'] =\
            'application/vnd.kii.SendPushMessageRequest+json'
        pushData = {'hello app topic push': self.message,\
            'identifier':time.time()}
        gcm = {'enabled': True}
        apns = {'enabled': True}
        body = {'data': pushData, 'gcm': gcm, 'apns': apns}
        jsonBody = json.dumps(body)
        self.logger.debug('path: %s', path)
        self.logger.debug('data %s', jsonBody)
        conn.request('POST', path, jsonBody, headers)
        response = conn.getresponse()
        self.logger.debug("status: %d", response.status)
        self.logger.debug("body: %s", json.load(response))

    def createAppBucketObject(self):
        self.logger.debug('create app bucket')
        conn = httplib.HTTPConnection(self.host)
        path = '/api/apps/{0}/buckets/{1}/objects'\
            .format(self.appId, self.appBucket)
        headers = {'x-kii-appid': self.appId, 'x-kii-appkey': self.appKey}
        headers['authorization'] = 'Bearer ' + self.token
        headers['content-type'] = 'application/json'
        obj = {'hoge':'dummy'}
        jsonObj = json.dumps(obj)
        self.logger.debug('path: %s', path)
        self.logger.debug('data %s', jsonObj)
        conn.request('POST', path, jsonObj, headers)
        response = conn.getresponse()
        self.logger.debug("status: %d", response.status)
        self.logger.debug("body: %s", json.load(response))

    def configureCollapseKey(self):
        self.logger.debug('configure collapse key')
        conn = httplib.HTTPConnection(self.host)
        path = '/api/apps/{0}/'.format(self.appId, self.appBucket)
        headers = {'x-kii-appid': self.appId, 'x-kii-appkey': self.appKey}
        headers['authorization'] = 'Bearer ' + self.token
        headers['content-type'] = 'application/vnd.kii.AppModificationRequest+json'
        obj = {'gcmCollapseKeyDefaultBehavior':self.setCollapseKey}
        jsonObj = json.dumps(obj)
        self.logger.debug('path: %s', path)
        self.logger.debug('data %s', jsonObj)
        conn.request('POST', path, jsonObj, headers)
        response = conn.getresponse()
        self.logger.debug("status: %d", response.status)

if __name__ == '__main__':
    helper = ApiHelper()
    helper.removeGCMKey()
    helper.setGCMKey()
    helper.getGCMKey()
    helper.removeJPushKey()
    helper.setJPushKey()
    helper.getJPushKey()
    helper.createAppTopic()
    helper.grantSubscriptionOfAppTopic()
    helper.createAppBucketObject()
    helper.configureCollapseKey()


