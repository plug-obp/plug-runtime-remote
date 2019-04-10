# plug-runtime-remote

`plug-runtime-remote` connects to a distant plug-runtime using a TCP connection.
In the following, we will refer to `plug-runtime-remote` as the `proxy runtime`.

## Remote protocol

The proxy runtime connects to a TCP socket that implements the ITransitionRelation and IAtomicPropositionEvaluator protocols. 
All requests are initiated from the proxy runtime to the distant plug-runtime.
All transfers are using `LITTLE_ENDIAN` encoding.

![Remote connection](images/remote.png)

The proxy runtime sends the following requests to the distant runtime:

- Initial configurations (1),
- Fireable transitions from (2)
- Fire transition (3).
- Register atomic propositions (4)
- Atomic proposition valuations (5)
- Extended atomic proposition valuations (6)
    
    
**Initial configurations**

Sends 
```
11
```
and waits for 
```
[4:configurations_count] 
(
    [4:configuration_size]
    [configuration_size:configuration]
){configurations_count}
```
**Fireable transitions from**

Sends 
```
12
[4:configuration_size]
[configuration_size:source_configuration]
```
and waits for 
```
[4:transitions_count]
[4:transition_size]
(
    [transition_size:transition]
){transitions_count}
```

**Fire transition**

Sends 
```
13
[4:configuration_size]
[configuration_size:source_configuration]
[4:transition_size]
[transition_size:transition_to_fire]
```
and waits for 
```
[4:configurations_count]
 (
    [4:configuration_size]
    [configuration_size:configuration]
 ){configurations_count} 
[4:payload_size]
[payload_size:payload]
```

**Register atomic propositions**

Sends 
```
14
[4:atoms_count]
(
    [4:atom_size]
    [atom:atom_size]
){atoms_count}
```
and no return is expected.

The `atom` is a 'utf-8` encoded string.

**Atomic proposition valuations**

Sends 
```
15
[4:configuration_size]
[configuration_size:source_configuration]
```
and waits for 
```
[4:value_count]
(
    [1:value]
){value_count}
```

**Extended atomic proposition valuations**

Sends 
```
15
[4:configuration_size]
[configuration_size:source_configuration]
[4:transition_size]
[transition_size:fireable_transition]
[4:payload_size]
[payload_size:payload]
[4:configuration_size]
[configuration_size:target_configuration]
```
and waits for 
```
[4:value_count]
(
    [1:value]
){value_count}
```

**Configuration items**

Sends 
```
110
[4:configuration_size]
[configuration_size:configuration]
```
and waits for 
```
[4:item_count]
(
   [item]
){item_count}

item = 
    [type_size:type]
    [name_size:name]
    [icon_size:icon]
    [4:children_count]
    (
        [item]
    ){children_count}
```

**Fireable transition description**

Sends 
```
111
[4:transition_size]
[transition_size:transition]
```
and waits for 
```
[4:text_length]
(
    [1:byte]
){text_length}
```
The resulting byte array is interpreted as UTF-8 
